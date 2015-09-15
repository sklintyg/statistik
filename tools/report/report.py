# -*- coding: utf-8 -*-
import sys, getopt
import re
from datetime import timedelta, date, time, datetime

THRESHOLD=5

def get_enheter(filename):
	""" Return all vårdenheter, key is vårdgivare+vårdenhet
	"""
	fd = open(filename)
	# Skip first line
	fd.readline()
	res = {}
	for line in fd:
		data = line.split('\t')
		# Vardgivare + Enhet
		key = data[0].strip()
		if key not in res:
			res[key] = data[1].strip() + '#' + key
		else:
			assert(0 == 1)
			enhet = res[key]
			assert(enhet == data[5])

	print "ret"
	return res

class Wideline:
	def __init__(self, columns):
		self.columns = {}
		i = 0
		for col in columns.split():
			self.columns[col.strip()] = i
			i += 1

	def get(self, values, key, mandatory=False):
		i = self.index(key)
		if i == None:
			if mandatory:
				raise Exception(key + " not found")
			return None

		if len(values) < i:
			raise Exception('Bad index ' + str(i) + " length " + str(len(values)))

		return values[i].strip()

	def index(self, key):
		if key in self.columns:
			return self.columns[key]
		return None
	
class VG:
	def __init__(self, name):
		self.name = name
		self.patienter = {}

	def add(self, intyg):
		# Add intyg to this vårdgivare
		if intyg.id in self.patienter:
			patient = self.patienter[intyg.id]
			patient.add(intyg)
		else:
			self.patienter[intyg.id] = Patient(intyg)

	def eval(self, group):
		for k,v in self.patienter.items():
			v.eval(group)
		return group.sum()

class Sjukfall:
	alpha_time = datetime(2000, 1, 1)

	def __init__(self, intyg):
                self.intyg = [ intyg ]
		self.start = intyg.start
		self.slut  = intyg.slut

	def __str__(self):
		s = ''
		for i in self.intyg:
			s = "{0}:{1}".format(s,i)
		return i

	def sjukgrad(self, start, slut):
		""" Get the sjukgrad from the 'last' intyg
		"""
		grad = None
		diff = False
		latest = None
		lakarintyg = None
		for i in self.intyg:
			if i.valid(start, slut):
				if grad is None:
					grad = i.sjukgrad
					latest = i.start
					lakarintyg = i.lakarintyg
				elif i.start > latest:
					grad = i.sjukgrad
					latest = i.start
					lakarintyg = i.lakarintyg
				elif i.start == latest:
					if i.lakarintyg > lakarintyg:
						grad = i.sjukgrad
						latest = i.start
						lakarintyg = i.lakarintyg
					elif i.lakarintyg == lakarintyg and i.sjukgrad > grad:
							grad = i.sjukgrad
							latest = i.start
							lakarintyg = i.lakarintyg

		assert(grad != None)

		return grad

	def kon(self):
		return self.intyg[0].kon

	def match_diagnos(self, diagnos, start, slut):
		for i in self.intyg:
			if i.valid(start, slut) and i.diagnoskategori == diagnos:
				return True
		return False

	def update(self, intyg):
		self.intyg.append(intyg)
		if intyg.start < self.start:
			self.start = intyg.start
		if intyg.slut > self.slut:
			self.slut = intyg.slut
		return True

	def befattningar(self, start, slut):
		""" Get all lakarbefattningar where intyg is in the period 
		"""
		lakare = {}
		
		for i in self.intyg:
			if i.valid(start,slut):
				if i.lakareid not in lakare:
					lakare[i.lakareid] = i
				else:
					intyg = lakare[i.lakareid]
					if i.start > intyg.start:
						lakare[i.lakareid] = i
		res = {}
		for k,v in lakare.items():
			l = []
			for b in v.befattning.split(','):
				l.append(b.strip())
			res[k] = l

		assert(len(res) > 0)

		return res

	def valid(self, start, slut):
		""" Check if this sjukfall is within the interval
		"""
		if self.slut < start:
			return False
		if self.start > slut:
			return False

		return True

	def age(self, start, slut):
		""" Välj den högsta åldern från sjukfall, enligt K30. Notera
		    att enligt K50 så väljs åldern utifrån hela sjukfallet
		"""
		alder = None
		for i in self.intyg:
			#if i.valid(start, slut):
			if True:
				if alder is None:
					alder = i.alder
				elif i.alder > alder:
					alder = i.alder

		assert(alder != None)

		return alder

	def match(self, intyg):
		# Check if intyg belongs in this group
		for i in self.intyg:
			if intyg.start >= i.start - 6 and intyg.start <= i.slut + 6:
				return self.update(intyg)
			if intyg.slut >= i.start - 6 and intyg.slut <= i.slut + 6:
				return self.update(intyg)
			if intyg.start < i.start and intyg.slut > i.slut:
				return self.update(intyg)

		return False

	def diagnos(self, start,slut):
		diagnos = None
		latest = None
		lakarintyg = None
		start_datum=None
		for i in self.intyg:
			if i.valid(start,slut):
				if diagnos is None or i.start > start_datum:
					diagnos = i.diagnos
					latest = i.start
					lakarintyg = i.lakarintyg
					start_datum = i.start
				elif i.start == start and i.lakarintyg > lakarintyg:
					diagnos = i.diagnos
					latest = i.start
					lakarintyg = i.lakarintyg
					start_datum = i.start

		assert(diagnos != None)
		
		return diagnos


	def get_intervals(self):
		""" Calculate number of sjukfall days for the given period
		"""
		intyg = [ i for i in self.intyg ]
		intervals = []
		# Split the intyg in intervals
		while len(intyg) > 0:
			interval = Interval(intyg.pop())
			intervals.append(interval)
			match = True
			# Keep matching until no matches
			while match:
				match = False
				tmp = []
				for i in intyg:
					if interval.match(i):
						match = True
					else:
						tmp.append(i)
	
				intyg = tmp
		return intervals

	def get_enhet_intervals(self, enhet):
		""" Calculate number of sjukfall days for the given period
		"""
		intyg = [ i for i in self.intyg if i.enhet == enhet ]
		intervals = []
		# Split the intyg in intervals
		while len(intyg) > 0:
			interval = Interval(intyg.pop())
			intervals.append(interval)
			match = True
			# Keep matching until no matches
			while match:
				match = False
				tmp = []
				for i in intyg:
					if interval.match(i):
						match = True
					else:
						tmp.append(i)
	
				intyg = tmp
		return intervals


	def days(self, start, slut):
		""" Enligt krav K50 ska hela sjukfallets längd räknas in
		"""
		intervals = self.get_intervals()
		tot = 0
		for i in intervals:
			tot += i.days()
		return tot

	def days_in_period(self, periods, enhet=None):
		result = []
		index  = 0
		if enhet:			
			intervals = self.get_enhet_intervals(enhet)
		else:
			intervals = self.get_intervals()
		for period in periods:
			days = 0
			for interval in intervals:
				days += interval.days_in_period(period[0], period[1])
			result.append((days, period[2]))
		return result

	def lan(self, start, slut):
		lan = None
		intyg = None
		for i in self.intyg:
			if i.valid(start, slut):
				if lan is None or i.start > intyg.start:
					lan = i.lan
					intyg = i
				elif i.start == intyg.start and i.lakarintyg > intyg.lakarintyg:
					lan = i.lan
					intyg = i
		assert(lan != None)
		return lan

	def lakare_alderkon(self, start, slut):
		""" Get all unqiue lakare
		"""
		res = {}
		for i in self.intyg:
			if not i.valid(start, slut):
				continue
			
			if i.lakareid not in res:
				res[i.lakareid] = (i.lakarkon, i.lakaralder, i.start)
			else:
				tup = res[i.lakareid]
				starttime = tup[2]
				if i.start > starttime:
					res[i.lakareid] = (i.lakarkon, i.lakaralder, i.start)
			
		return res

	def lakare(self, enhet, start, slut):
		res = []
		for i in self.intyg:	
			assert(len(i.lakareid) > 0)
			assert(i.lakareid != None)
			if i.valid(start, slut) and i.enhet == enhet:
				if i.lakareid not in res:
					res.append(i.lakareid)
		return res

	def match_enhet(self, start, slut, enhet):
		for i in self.intyg:
			if i.valid(start, slut) and i.enhet == enhet:
				return True
		return False

	def enheter(self, start, slut):
		res = []
		for i in self.intyg:
			if i.valid(start, slut):
				if i.enhet not in res:
					res.append(i.enhet) 
		return res

	def over_90(self, periods):
		result = []
		intervals = self.get_intervals()
		intervals.sort(key=lambda i : i.start)
		days = 0
		tot  = 0
		for interval in intervals:
			days = interval.days()
			test = 0
			prev = 0
			interval.intyg.sort(key=lambda i : i.start)
			# Make som basic checks that the periods add up
			for i in interval.intyg:
				test += i.days()
				assert (prev < i.start)
			assert(test == interval.days())

			for i in interval.intyg:
				tot += i.days()
				if tot > 90:
					# Include the sjukfall from the intygs
					# start to the sjukfalls slut
					for p in periods:
						if self.slut < p[0]:
							continue
						if i.intyg_start > p[1]:
							continue

						result.append(p[2])
					return result
		return []	
	
class Patient:
        def __init__(self, intyg):
                self.intyg = []
		self.kon = intyg.kon
		self.add(intyg)

        def add(self, intyg):
                self.intyg.append(intyg)

        def eval(self, res):
                tot = 0
		groups = []
		
		intyg = [ i for i in self.intyg ]

		while len(intyg) > 0:
			sjukfall = Sjukfall(intyg.pop())
			groups.append(sjukfall)
			match = True
			# Keep matching until no matches
			while match:
				match = False
				tmp = []
				for i in intyg:
					if sjukfall.match(i):
						match = True
					else:
						tmp.append(i)
				intyg = tmp

		for group in groups:
			# Add each sjukfall to the result set
			res.add(group)

class Intyg:
	def __init__(self, data, wideline, enheter):
		values = data.split('\t')
		self.lakarintyg = int(wideline.get(values, 'lakarintyg', True))
		self.id         = wideline.get(values, 'patientid', True)
		self.vardgivare = wideline.get(values, 'vardgivareid', True)
		self.start      = int(wideline.get(values, 'startdatum', True))
		self.slut       = int(wideline.get(values, 'slutdatum', True))
		self.diagnos    = wideline.get(values, 'diagnoskapitel', True)
		self.kon        = int(wideline.get(values, 'kon', True))
		self.alder      = int(wideline.get(values, 'alder', True))
		self.sjukgrad   = int(wideline.get(values, 'sjukskrivningsgrad'))
		if self.sjukgrad:
			self.sjukgrad = int(self.sjukgrad)
		self.befattning = wideline.get(values, 'lakarbefattning')
		self.lakaralder = wideline.get(values, 'lakaralder')
		if self.lakaralder:
			self.lakaralder = int(self.lakaralder)
		self.lakareid   = wideline.get(values, 'lakareid')
		self.lakarkon   = wideline.get(values, 'lakarkon')
		if self.lakarkon:
			self.lakarkon = int(self.lakarkon)
		self.enhet      = wideline.get(values, 'enhet')
		self.lkf = wideline.get(values, 'lkf')
		if self.lkf:
			self.lan = self.lkf[0:2]
		self.diagnoskategori = wideline.get(values, 'diagnoskategori')
		if enheter:
			self.enhet = enheter[self.enhet]

	def valid(self, start, slut):
		""" Check if this sjukfall is within the interval
		"""
		if self.slut < start:
			return False
		if self.start > slut:
			return False

		return True

	def tostring(self):
		return "[{0},{1},{2},{3},{4},{5}]".format(self.id,self.start,self.slut,self.alder,self.sjukgrad, self.enhet)

	def __str__(self):
		return "[{0},{1}]".format(self.start,self.slut)


class Period:
	def __init__(self, start, slut):
		self.intyg_start = start
		self.start = start
		self.slut  = slut
		self.valid  = True

	def update(self, period):
		""" Update the periods with possible new start point.
		    Period might be invalidated in case of overlap. Note
		    that both periods may be updated
		"""
		if self.slut < period.start:
			return
		if self.start > period.slut:
			return

		if not self.valid or not period.valid:
			return

		if period.start <= self.start and period.slut >= self.slut:
			self.valid = False
			return

		# Update start
		if period.start < self.start and period.slut <= self.slut:
			self.start = period.slut + 1

		if period.start >= self.start and period.slut <= self.slut:
			period.valid = False

		if period.slut > self.slut and period.start > self.start:
			period.start = self.slut + 1

		#print self.days(),period.days()

	def __str__(self):
		return "({0},{1},{2})".format(self.intyg_start,self.start,self.slut)

	def days(self):
		if not self.valid:
			return 0
		return self.slut - self.start + 1

class Interval:
	def __init__(self, intyg):
		self.start = intyg.start
		self.slut  = intyg.slut
		self.intyg = [Period(self.start, self.slut)]

	def update(self, start, slut):
		if start < self.start:
			self.start = start
		if slut > self.slut:
			self.slut = slut

		period = Period(start,slut)
		for i in self.intyg:
			i.update(period)
		self.intyg.append(period)
		
		return True

	def match(self, intyg):
		if intyg.start >= self.start and intyg.start <= self.slut:
			return self.update(intyg.start, intyg.slut)
		if intyg.slut >= self.start and intyg.slut <= self.slut:
			return self.update(intyg.start, intyg.slut)
		if intyg.start < self.start and intyg.slut > self.slut:
			return self.update(intyg.start, intyg.slut)

		return False

	def days(self):
		return self.slut - self.start + 1

	def days_in_period(self, start, slut):
		if self.slut < start:
			return 0
                if self.start > slut:
                        return 0

		first = self.start
		last  = self.slut

		if start > first:
			first = start
		if slut < last:
			last  = slut

                return last - first + 1


class RuleLakare:
	def __init__(self, enhet):
		self.enhet = enhet

	def key(self, sjukfall, start, slut):
		return sjukfall.lakare(self.enhet, start, slut)

	def check(self, wideline):
		assert(wideline.index('lakareid') != None)
		

class RuleLakaralderKon:
	def key(self, sjukfall, start, slut):
		lakare = sjukfall.lakare_alderkon(start, slut)
		list = []
		for kon,tup in lakare.items():
			list.append(self.alder(tup[0], tup[1]))
		return list

	def alder(self, kon, age):
		prefix = ''
		if kon == 1:
			prefix = "Manlig läkare "
		elif kon == 2:
			prefix = "Kvinnlig läkare "
		else:
			return "Okänt kön and ålder"

		if age < 30:
			return prefix + "under 30 år"
		if age >= 30 and age <= 39:
			return prefix + "30-39"
		if age >= 40 and age <= 49:
			return prefix + "40-49"
		if age >= 50 and age <= 59:
			return prefix + "50-59"
		return prefix + "över 59"

	def check(self, wideline):
		assert(wideline.index('lakareid') != None)
		assert(wideline.index('lakaralder') != None)
		assert(wideline.index('lakarkon') != None)


class RuleLakarbefattning:
	def __init__(self):
		self.befattningar = { "201010" : "Överläkare", "201011" : "Distriktsläkare/Specialist allmänmedicin", "201012" : "Skolläkare", "201013" : "Företagsläkare", "202010" : "Specialistläkare", "203010" : "Läkare legitimerad, specialiseringstjänstgöring", "203090" : "Läkare legitimerad, annan", "204010" : "Läkare ej legitimerad, allmäntjänstgöring", "204090" : "Läkare ej legitimerad, annan" } 

	def key(self, sjukfall, start, slut):
		befattningar = sjukfall.befattningar(start, slut)
		list = []
		for lakare,befattning in befattningar.items():
			self.befattning(list, befattning)
		return list

	def befattning(self, list, key):
		""" Om läkaren har en läkarbefattning och en icke-läkarbefattning 
		ska icke-läkarbefattningen inte räknas med
		"""
		ejlakare = False
		lakare = False
		i = 0
		for b in key:
			i = 1
			if b in self.befattningar:
				lakare = True
				list.append(self.befattningar[b])
			else:
				ejlakare = True
		assert(i == 1)
		if ejlakare and not lakare:
			list.append("Ej läkarbefattning")

	def check(self, wideline):
		assert(wideline.index('lakarbefattning') != None)
		assert(wideline.index('lakaralder') != None)

class RuleSjuklangd90:
	def __init__(self, year, month, months, enhet=None):
		self.enhet = enhet
		print "Enhet=",enhet
		self.periods = self.make_periods(year, month, months)

	def make_periods(self, year, month, months):
		alpha = datetime(2000, 1, 1)
		i = 0
		mlist = []
		prev = 0
		tag = "2000/1"
		while i < months + 1:
			i += 1
			if month > 12:
				year += 1
				month = 1
			beta  = datetime(year, month, 1)
			delta = beta - alpha
			mlist.append((prev, delta.days - 1,tag))
			tag = str(year) + '/' + str(month)
			prev = delta.days
			month += 1

		for tup in mlist:
			print alpha + timedelta(tup[0]),alpha + timedelta(tup[1])

		return mlist

	def key(self, sjukfall, start, slut):
		return sjukfall.over_90(self.periods)
		'''
		months = sjukfall.days_in_period(self.periods, self.enhet)
		num = months[0][0]
		result = []
		for i in months[1:]:
			num += i[0]
			assert(i[0] <= 31)
			if num > 90 and i[0] > 0:
				result.append(i[1])

		return result
		'''

	def check(self, wideline):
		pass

class RuleJamfor:
	def __init__(self, diagnos):
		self.diagnos = [diagnos]
		
		if diagnos.find('-') != -1:
			self.diagnos = []
			data = diagnos.split('-')
			self.diagnos.append(data[0])
			m = int(data[1])
			i = int(data[0][1:])
			d = data[0][0]
			print i,m,d
			i += 1
			while i < m:
				if i < 10:
					self.diagnos.append(d + '0' + str(i))
				else:
					self.diagnos.append(d + str(i))
				i += 1
			print self.diagnos
				

	def key(self, sjukfall, start, slut):
		res = []
		for diagnos in self.diagnos:
			if sjukfall.match_diagnos(diagnos, start, slut):
				res.append(diagnos)
		return res

	def check(self, wideline):
		assert(wideline.index('diagnoskategori') != None)

class RuleSjuklangd:
	def key(self, sjukfall, start, slut):
		days = sjukfall.days(start, slut)
		if days < 15:
			return '<15'
		elif days >= 15 and days <= 30:
			return '15-30'
		elif days >= 31 and days <= 90:
			return '31-90'
		elif days >= 91 and days <= 180:
			return '91-180'
		elif days >= 181 and days <= 365:
			return '181-365'
		else:
			return '>365'

	def check(self, wideline):
		pass
	
class RuleLan:
	def key(self, sjukfall, start, slut):
		key = sjukfall.lan(start, slut)
		return self.lan(key)

	def lan(self, key):
		if key is None:
			return []
		if key == "10":
			return "Blekinge län"
		if key == "20":
			return "Dalarnas län"
		if key == "13":
			return "Hallands län"
		if key == "08":
			return "Kalmar län" 
		if key == "07":
			return "Kronobergs län"
		if key == "09":
			return "Gotlands län" 
		if key == "21":
			return "Gävleborgs län"
		if key == "23":
			return "Jämtlands län"
		if key == "06":
			return "Jönköpings län"
		if key == "25":
			return "Norrbottens län"
		if key == "12":
			return "Skåne län"
		if key == "01":
			return "Stockholms län"
		if key == "04":
			return "Södermanlands län"
		if key == "03":
			return "Uppsala län"
		if key == "17":
			return "Värmlands län"
		if key == "24":
			return "Västerbottens län"
		if key == "22":
			return "Västernorrlands län"
		if key == "19":
			return "Västmanlands län"
		if key == "14":
			return "Västra Götalands län"
		if key == "18":
			return "Örebro län"
		if key == "05":
			return "Östergötlands län"
		assert(key == "00")
		return "Okänt län"

	def check(self, wideline):
		assert(wideline.index('lkf') != None)

class RuleSjukfallEnhet:
	def __init__(self, enhet):
		self.enhet = enhet
		
	def key(self, sjukfall, start, slut):
		if sjukfall.match_enhet(start, slut, self.enhet):
			return self.enhet
		return None

	def check(self, wideline):
		assert(wideline.index('enhet') != None)

class RuleSjukfallEnheter:
	def key(self, sjukfall, start, slut):
		return sjukfall.enheter(start, slut)

	def check(self, wideline):
		assert(wideline.index('enhet') != None)

class RuleSjukfall:
	def key(self, sjukfall, start, slut):
		return "default"

	def check(self, wideline):
		pass

class RuleDiagnos:
	def key(self, sjukfall, start, slut):
		return sjukfall.diagnos(start,slut)

	def check(self, wideline):
		assert(wideline.index('diagnoskapitel'))

class RuleSjukgrad:
	def key(self, sjukfall, start, slut):
		grad = sjukfall.sjukgrad(start,slut)
		if grad != 25 and grad != 50 and grad != 75 and grad != 100:
			raise Exception("Invalid sjukgrad {}".format(grad))
		return str(grad)

	def check(self, wideline):
		assert(wideline.index('sjukskrivningsgrad') != None)


class RuleAlder:
	def key(self, sjukfall, start, slut):
		age = sjukfall.age(start, slut)
		if age < 21:
			return '<21'
		elif age >= 21 and age <= 25:
			return '21-25'
		elif age >= 26 and age <= 30:
			return '26-30'
		elif age >= 31 and age <= 35:
			return '31-35'
		elif age >= 36 and age <= 40:
			return '36-40'
		elif age >= 41 and age <= 45:
			return '41-45'
		elif age >= 46 and age <= 50:
			return '46-50'
		elif age >= 51 and age <= 55:
			return '51-55'
		elif age >= 56 and age <= 60:
			return '56-60'
		else:
			return '>60'

	def check(self, wideline):
		assert(wideline.index('alder') != None)

class Group:
	def __init__(self, rule=None, threshold=False, agg=None, start=None, end=None):
		self.group = {}
		self.agg   = agg
		self.threshold = threshold
		self.rule = rule
		self.start = start
		self.end = end
		self.notvalid = 0

	def __add(self, key, num, kon):
		tup = (0,0)
		if key in self.group:
			tup = self.group[key]

		if kon == 1:
			self.group[key] = (tup[0] + num, tup[1])
		elif kon == 2:
			self.group[key] = (tup[0], tup[1] + num)
		else:
			raise Exception('Bad kon number: {} '.format(kon))

	def add(self, sjukfall):
		if self.start and self.end:
			if not sjukfall.valid(self.start, self.end):
				self.notvalid += 1
				return

		key = self.rule.key(sjukfall, self.start, self.end)
		if key:
			if isinstance(key, list):
				for k in key:
					self.__add(k, 1, sjukfall.kon())
			else:
				self.__add(key, 1, sjukfall.kon())

	def sum(self):
		tot = 0
		for k,v in self.group.items():
			if self.threshold is False or v[0] >= THRESHOLD:
				tot += v[0]
				if self.agg:
					self.agg.__add(k,v[0], 1)
			if self.threshold is False or v[1] >= THRESHOLD:
				tot += v[1]
				if self.agg:
					self.agg.__add(k,v[1], 2)
		return tot

	def log(self):
		for key in sorted(self.group):
			v = self.group[key]
			print "{0} ({1}, {2}) => {3}".format(key,v[1],v[0], v[0] + v[1])

def date2days(string, alpha):
	data = string.split('-')
	assert(len(data) == 3)
	date = datetime(int(data[0]), int(data[1]), int(data[2])) - alpha
	return date.days

def usage():
	print '''Usage: python report.py -i <start-interval:end-interval> [options] <rule>
options:
	-t		Use threshold (5) for vårdgivare and male/female
	-i <start:end>	The interval for the sjukfall, must be on the following format:
			YYYY-MM-DD:YYYY-MM-DD
	-p		Validate personid

rule:
	-s		Group on sjukfall
	-d		Group on diagnos (kapitel)
	-a		Group on age
	-g		Group on sjukskrivningsgrad
	-l		Group on sjukskrivningslängd
	-n		Group on län
	-k		Group on läkarbefattning
	-K		Group on läkarålder och kön
	-L <enhet>	Group on läkare for a specific enhet
	-E <enhet>	Group on sjukfall and specific enhet
	-e		Group on sjukfall and enheter
	-N		Group on sjukfall longer than 90 days
	-9 <enhet>	Group on sjukfall longer than 90 days for enhet
	-v enheter
'''
	

def main(argv):
	threshold = False
	check_personid = False
	alpha = datetime(2000, 1, 1)
	now = datetime.now()
	end_limit = datetime(now.year + 5, now.month, now.day) - alpha
	start_limit = datetime(2010,1,1) - alpha
	start_interval = None
	end_interval   = None
	interval = None
	rule = None
	enheter = None

	opts, args = getopt.getopt(argv,"tdapglni:ekKL:hsE:Nj:9:v:")
	for opt, arg in opts:
		if opt == '-t':
			threshold = True
		elif opt == '-e':
			rule = RuleSjukfallEnheter()
		elif opt == '-E':
			rule = RuleSjukfallEnhet(arg.strip())
		elif opt == '-s':
			rule = RuleSjukfall()
		elif opt == '-d':
			rule = RuleDiagnos()
		elif opt == '-a':
			rule = RuleAlder()
		elif opt == '-p':
			check_personid = True
		elif opt == '-g':
			rule = RuleSjukgrad()
		elif opt == '-l':
			rule = RuleSjuklangd()
		elif opt == '-n':
			rule = RuleLan()
		elif opt == '-N':
			assert(interval != None)
			data = interval[0].split('-')
			rule = RuleSjuklangd90(int(data[0]),int(data[1]),18)
		elif opt == '-9':
			assert(interval != None)
			data = interval[0].split('-')
			rule = RuleSjuklangd90(int(data[0]),int(data[1]),18, arg.strip())
		elif opt == '-j':
			rule = RuleJamfor(arg.strip())
		elif opt == '-i':
			interval = arg.split(':')
			assert(len(interval) == 2)
			start_interval = date2days(interval[0], alpha)
			end_interval   = date2days(interval[1], alpha)
			#print start_interval
			#print end_interval
		elif opt == '-k':
			rule = RuleLakarbefattning()
		elif opt == '-K':
			rule = RuleLakaralderKon()
		elif opt == '-L':
			rule = RuleLakare(arg.strip())
		elif opt == '-h':
			return usage()
		elif opt == '-v':
			enheter = get_enheter(arg.strip())

	if start_interval is None or end_interval is None:
		print "Interval is missing"
		return

	if rule is None:
		print "Group rule is missing"
		return

	# Read the column names, must be first line
	wideline = Wideline(sys.stdin.readline())

	# Check that the right columns was included for this test
	rule.check(wideline)

	vgmap = {}
	for line in sys.stdin:
		intyg = Intyg(line, wideline, enheter)

		# Check that slutdatum is not less than startdatum
		if intyg.slut < intyg.start:
			print "disregarding intyg: ", intyg.start, intyg.slut
			continue

		if not re.search('[\d]{8}-[\d]{4}', intyg.id):
			print "Invalid patientid: ", intyg.id
			continue
	
		# Make a simple check of personid
		if check_personid:			
			year = int(intyg.id[0:4])
			month = int(intyg.id[4:6])
			day = int(intyg.id[6:8])
			if month == 0 or day == 0:
				print "Invalid personid: " + intyg.id
				continue
			if month > 12 or day > 31 or year > now.year:
				print "Invalid personid2: " + intyg.id
				continue
		

		# Start- eller slutdatum för sjukskrivningsperioden får inte vara mer än fem år fram i tiden.
		# Ingen idé att kolla startdatum för då skulle inte intyget kommit med i urvalet
		if intyg.slut > end_limit.days:
			print "Ignoring intyg with bad end date: ", intyg.slut
			continue

		# Startdatumet för sjukskrivningsperioden får inte vara före 2010-01-01
		if intyg.start < start_limit.days:
			print "Ignoring intyg with invalid start date: ", intyg.start
			continue

	        if intyg.vardgivare in vgmap:
        	        vg = vgmap[intyg.vardgivare]
                	vg.add(intyg)
	        else:
        	        vg = VG(intyg.vardgivare)
        	        vg.add(intyg)
			vgmap[intyg.vardgivare] = vg
	tot = 0
	agg = Group()
	# Summarize all data from all vårdgivare
	for k,v in vgmap.items():
		tot += v.eval(Group(rule, threshold, agg, start_interval, end_interval))
	print tot

	agg.log()

if __name__ == "__main__":
	main(sys.argv[1:])
