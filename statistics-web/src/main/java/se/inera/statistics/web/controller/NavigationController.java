/**
 * Copyright (C) 2012 Callista Enterprise AB <info@callistaenterprise.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="")
public class NavigationController {

	@RequestMapping(value="/start", method=RequestMethod.GET)
	public String displayStart() {
		return "start";
	}

	@RequestMapping(value="/age", method=RequestMethod.GET)
	public String displayAge() {
		return "age";
	}

	@RequestMapping(value="/duration", method=RequestMethod.GET)
	public String displayDuration() {
		return "duration";
	}
	
	@RequestMapping(value="/monthwise", method=RequestMethod.GET)
	public String displayMonthwise() {
		return "monthwise";
	}
	
	@RequestMapping(value="/sicknessgroups", method=RequestMethod.GET)
	public String displaySickneessGroups() {
		return "sicknessgroups";
	}
	
	@RequestMapping(value="/careunit", method=RequestMethod.GET)
	public String displayCareunit() {
		return "careunit";
	}
}
