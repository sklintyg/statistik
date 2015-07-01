<div class="statistics accordion hidden-xs" id="statistics-menu-accordion">
  <div class="accordion-group" id="national-statistics-menu-group">
    <h2 class="hidden-header"><span message key="statistics.hidden-header.nationell-navigering"></span></h2>
    <!-- NATIONAL STATISTIC MENU -->
    <div class="accordion-heading statistics-menu">
      <div class="accordion-toggle first-level-menu" id="national-statistics-toggle"
           data-parent="#statistics-menu-accordion"
           data-ng-class="{active: showNational, collapsed: !showNational}"
           data-ng-click="toggleNationalAccordion()">
        <span class="statistics-menu-heading"><span message key="nav.national-header"></span></span><i class="statistict-left-menu-expand-icon"></i>
      </div>
    </div>
    <div id="national-statistics-collapse" class="accordion-body collapse navigation-group"
         data-ng-class="{in: showNational}">
      <div class="accordion-inner">
        <ul id="national-statistic-menu-content" class="nav nav-list">
          <li><a data-ng-href="#/nationell/oversikt{{queryString}}" id="navOverviewLink"
                 ctrlname="NationalOverviewCtrl" navigationaware><span message key="nav.oversikt"></span></a></li>
          <li><a data-ng-href="#/nationell/sjukfallPerManad{{queryString}}" id="navCasesPerMonthLink"
                 ctrlname="NationalCasesPerMonthCtrl" navigationaware><span message key="nav.sjukfall-totalt"></span></a>
          </li>
          <li>
            <a class="menu-item-has-childs"
               data-ng-href="#/nationell/diagnosgrupp{{queryString}}" id="navDiagnosisGroupsLink"
               ctrlname="NationalDiagnosgruppCtrl" navigationaware><span message key="nav.diagnosgrupp"></span>
              <i class="statistict-left-menu-expand-icon" data-toggle="collapse" href="#sub-menu-diagnostics"></i>
            </a>

          </li>
          <ul id="sub-menu-diagnostics" class="nav nav-list sub-nav-list accordion-body in collapse">
            <li><a data-ng-href="#/nationell/diagnosavsnitt{{queryString}}" id="navDiagnosisSubGroupsLink"
                   ctrlname="NationalDiagnosavsnittCtrl" navigationaware><span message key="nav.enskilt-diagnoskapitel"></span></a></li>
          </ul>
          <li><a data-ng-href="#/nationell/aldersgrupper{{queryString}}" id="navAgeGroupsLink"
                 ctrlname="NationalAgeGroupCtrl" navigationaware><span message key="nav.aldersgrupp"></span></a></li>
          <li><a data-ng-href="#/nationell/sjukskrivningsgrad{{queryString}}" id="navSickLeaveDegreeLink"
                 ctrlname="NationalDegreeOfSickLeaveCtrl"
                 navigationaware><span message key="nav.sjukskrivningsgrad"></span></a></li>
          <li><a data-ng-href="#/nationell/sjukskrivningslangd{{queryString}}" id="navSickLeaveLengthLink"
                 ctrlname="NationalSickLeaveLengthCtrl"
                 navigationaware><span message key="nav.sjukskrivningslangd"></span></a></li>
          <li>
            <a data-ng-href="#/nationell/lan{{queryString}}" id="navCountyLink" class="has-collapse"
               ctrlname="NationalCasesPerCountyCtrl" navigationaware><span message key="nav.lan"></span>
              <i class="statistict-left-menu-expand-icon accordion-toggle" data-toggle="collapse" href="#sub-menu-cases-per-county"></i>
            </a>
          </li>
          <ul id="sub-menu-cases-per-county"
              class="nav nav-list sub-nav-list accordion-body in collapse">
            <li>
              <a class="last-item-in-menu rounded-bottom"
                 data-ng-href="#/nationell/andelSjukfallPerKon{{queryString}}" id="navCasesPerSexLink"
                 ctrlname="NationalCasesPerSexCtrl" navigationaware><span message key="nav.lan-andel-sjukfall-per-kon"></span>
              </a>
            </li>
          </ul>
        </ul>
      </div>
    </div>
  </div>

  <!-- LANDSTING MENU -->
    <div class="accordion-group" id="landsting-statistics-menu-group" data-ng-show="hasLandstingAccess">
      <h2 class="hidden-header"><span message key="statistics.hidden-header.landsting-navigering"></span></h2>
      <div class="accordion-heading statistics-menu">
        <div class="accordion-toggle first-level-menu" id="landsting-statistics-toggle"
             data-parent="#statistics-menu-accordion"
             data-ng-class="{active: showLandsting, collapsed: !showLandsting}"
             data-ng-click="toggleLandstingAccordion()">
            <span class="statistics-menu-heading"><span message key="nav.landsting-header"></span></span><i class="statistict-left-menu-expand-icon"></i>
        </div>
      </div>
      <div id="landsting-statistics-collapse" class="accordion-body collapse navigation-group" data-ng-class="{in: showLandsting}">
        <div class="accordion-inner">
          <ul id="landsting-statistic-menu-content" class="nav nav-list">
            <li><a ng-class="{'not-active': !landstingAvailable}" data-ng-href="#/landsting/sjukfallPerManad{{queryString}}" id="navLandstingCasesPerMonthLink" ctrlname="LandstingCasesPerMonthCtrl" navigationaware><span message key="nav.sjukfall-totalt"></span></a></li>
            <li><a ng-class="{'not-active': !landstingAvailable}" data-ng-href="#/landsting/sjukfallPerEnhet{{queryString}}" id="navLandstingCasesPerEnhetLink" ctrlname="LandstingCasesPerBusinessCtrl" navigationaware><span message key="nav.vardenhet"></span></a></li>
            <li><a ng-class="{'not-active': !landstingAvailable}" data-ng-href="#/landsting/sjukfallPerListningarPerEnhet{{queryString}}" id="navLandstingCasesPerPatientsPerEnhetLink" ctrlname="LandstingCasesPerPatientsPerBusinessCtrl" navigationaware><span message key="nav.landsting.listningsjamforelse"></span></a></li>
            <li><a data-ng-href="#/landsting/om{{queryString}}" id="navLandstingAboutLink" ctrlname="LandstingAboutCtrl" navigationaware><span message key="nav.landsting.om"></span></a></li>
            <li data-ng-show="isLandstingAdmin"><a data-ng-href="#/landsting/filuppladdning{{queryString}}" ctrlname="LandstingFileUploadCtrl" navigationaware><span message key="nav.landsting.filuppladdning"></span></a></li>
          </ul>
        </div>
      </div>
    </div>
  <c:if test="${loginVisible}">
    <div class="accordion-group" id="business-statistics-menu-group">
      <h2 class="hidden-header"><span message key="statistics.hidden-header.business-navigering"></span></h2>

      <!-- BUSINESS STATISTIC MENU -->
      <div class="accordion-heading statistics-menu">
        <div class="accordion-toggle first-level-menu" id="business-statistics-toggle"
             data-parent="#statistics-menu-accordion"
             data-ng-class="{active: showOperation, collapsed: !showOperation, disabled: !enableVerksamhetMenu}"
             data-ng-click="toggleOperationAccordion()">
          <span class="statistics-menu-heading" data-ng-bind="organisationMenuLabel"></span>
          <i class="statistict-left-menu-expand-icon"></i>
          <!-- Inloggad: Enbart "Verksamhetsstatistik" -->
        </div>
      </div>
      <div id="business-statistics-collapse" class="accordion-body collapse navigation-group"
           data-ng-class="{in: showOperation}">
        <div class="accordion-inner">
          <ul id="business-statistic-menu-content" class="nav nav-list">
            <li><a data-ng-href="#/verksamhet/oversikt{{queryString}}"
                   ctrlname="businessOverviewCtrl" navigationaware><span message key="nav.oversikt"></span></a></li>
            <li ng-show="isProcessledare || isDelprocessledare"><a data-ng-href="#/verksamhet/sjukfallperenhet{{queryString}}"
                                                                   id="navBusinessCasesPerMonthLink" ctrlname="VerksamhetCasesPerBusinessCtrl"
                                                                   navigationaware><span message key="nav.vardenhet"></span></a></li>
            <li><a data-ng-href="#/verksamhet/sjukfallPerManad{{queryString}}"
                   id="navBusinessCasesPerMonthLink" ctrlname="VerksamhetCasesPerMonthCtrl"
                   navigationaware><span message key="nav.sjukfall-totalt"></span></a></li>
            <li>
              <a class="menu-item-has-childs"
                 data-ng-href="#/verksamhet/diagnosgrupp{{queryString}}"
                 id="navBusinessDiagnosisGroupsLink" ctrlname="VerksamhetDiagnosgruppCtrl"
                 navigationaware><span message key="nav.diagnosgrupp"></span>
                <i class="statistict-left-menu-expand-icon accordion-toggle" data-toggle="collapse" href="#sub-menu-business-diagnostics"></i>
              </a>
            </li>
            <ul id="sub-menu-business-diagnostics"
                class="nav nav-list sub-nav-list accordion-body in collapse">
              <li><a data-ng-href="#/verksamhet/diagnosavsnitt{{queryString}}"
                     id="navBusinessDiagnosisSubGroupsLink"
                     ctrlname="VerksamhetDiagnosavsnittCtrl" navigationaware><span message key="nav.enskilt-diagnoskapitel"></span>
              </a>
              </li>
              <li><a data-ng-href="#/verksamhet/jamforDiagnoser{{queryString}}"
                     id="navBusinessCompareDiagnosisLink"
                     ctrlname="VerksamhetCompareDiagnosisCtrl" navigationaware><span message key="nav.jamfor-diagnoser"></span>
              </a>
              </li>
            </ul>
            <li>
              <a data-ng-href="#/verksamhet/aldersgrupper{{queryString}}"
                 id="navBusinessAgeGroupsLink" ctrlname="VerksamhetAgeGroupCtrl"
                 navigationaware><span message key="nav.aldersgrupp"></span>
              </a>
            </li>
            <li><a data-ng-href="#/verksamhet/sjukskrivningsgrad{{queryString}}"
                   id="navBusinessSickLeaveDegreeLink"
                   ctrlname="VerksamhetDegreeOfSickLeaveCtrl" navigationaware><span message key="nav.sjukskrivningsgrad"></span></a>
            </li>
            <li>
              <a class="menu-item-has-childs has-collapse"
                 data-ng-href="#/verksamhet/sjukskrivningslangd{{queryString}}"
                 id="navBusinessSickLeaveLengthLink"
                 ctrlname="VerksamhetSickLeaveLengthCtrl" navigationaware><span message key="nav.sjukskrivningslangd"></span>
                <i class="statistict-left-menu-expand-icon accordion-toggle" data-toggle="collapse" href="#sub-menu-business-sick-leave-length"></i>
              </a>
            </li>
            <ul id="sub-menu-business-sick-leave-length"
                class="nav nav-list sub-nav-list accordion-body in collapse">
              <li><a data-ng-href="#/verksamhet/langasjukskrivningar{{queryString}}"
                     id="navBusinessMoreNinetyDaysSickLeaveLink"
                     ctrlname="VerksamhetLongSickLeavesCtrl" navigationaware><span message key="nav.sjukskrivningslangd-mer-an-90-dagar"></span></a></li>
            </ul>
            <li ng-show="!isProcessledare"><a class="last-item-in-menu border-bottom" data-ng-href="#/verksamhet/sjukfallperlakare{{queryString}}"
                                              id="navBusinessCasesPerLakareLink" ctrlname="VerksamhetCasesPerLakareCtrl"
                                              navigationaware><span message key="nav.lakare"></span></a>
            </li>
            <li><a class="last-item-in-menu border-bottom" data-ng-href="#/verksamhet/sjukfallperlakaresalderochkon{{queryString}}"
                   id="navBusinessCasesPerLakaresAlderOchKonLink" ctrlname="VerksamhetLakaresAlderOchKonCtrl"
                   navigationaware><span message key="nav.lakaralder-kon"></span></a>
            </li>
            <li><a class="last-item-in-menu rounded-bottom" data-ng-href="#/verksamhet/sjukfallperlakarbefattning{{queryString}}"
                   id="navBusinessCasesPerLakarbefattningLink" ctrlname="VerksamhetLakarbefattningCtrl"
                   navigationaware><span message key="nav.lakarbefattning"></span></a>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </c:if>
  <div class="accordion-group" id="about-statistics-menu-group">
    <h2 class="hidden-header"><span message key="statistics.hidden-header.about-navigering"></span></h2>
    <!-- ABOUT STATISTIC MENU -->
    <div class="accordion-heading statistics-menu">
      <div class="accordion-toggle first-level-menu"
           data-ng-class="{active: showAbout, collapsed: !showAbout}"
           data-ng-click="toggleAboutAccordion()">
        <span class="statistics-menu-heading"><span message key="nav.about-header"></span></span><i class="statistict-left-menu-expand-icon"></i>
      </div>
    </div>
    <div id="about-statistics-collapse" class="accordion-body collapse navigation-group"
         data-ng-class="{in: showAbout}">
      <div class="accordion-inner">
        <ul id="about-statistic-menu-content" class="nav nav-list">
          <li><a class="first-item-in-menu" data-ng-href="#/om/tjansten{{queryString}}"
                 ctrlname="AboutServiceCtrl" navigationaware><span message key="nav.allmant-om-tjansten"></span></a></li>
          <li><a data-ng-href="#/om/inloggning{{queryString}}" ctrlname="AboutLoginCtrl" navigationaware><span message key="nav.inloggning-behorighet"></span></a></li>
          <li><a data-ng-href="#/om/vanligafragor{{queryString}}" ctrlname="AboutFaqCtrl" navigationaware><span message key="nav.faq"></span></a></li>
          <li><a class="last-item-in-menu no-border-top rounded-bottom"
                 data-ng-href="#/om/kontakt{{queryString}}" ctrlname="AboutContactCtrl" navigationaware><span message key="nav.kontakt-support"></span></a></li>
        </ul>
      </div>
    </div>
  </div>
</div>
