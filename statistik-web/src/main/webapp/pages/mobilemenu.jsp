<!-- MOBILE NAVIGATION START -->
<nav class="navbar navbar-default hidden-sm hidden-md hidden-lg" role="navigation">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header navbar-header-margins">
      <span class="navbar-brand pull-left"><span message key="lbl.mobile-menu"></span></span>
      <button type="button" class="navbar-toggle collapsed pull-left" data-toggle="collapse in" data-target="#navbar-mobile-menu-national" ng-click="isCollapsed = !isCollapsed">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <c:if test="${loginVisible}">
        <div class="col-xs-6 hidden-sm hidden-md hidden-lg pull-right">
          <div id="business-login-container" class="pull-right" ng-hide="isLoggedIn">
            <a class="btn" data-ng-click="loginClicked('${applicationScope.loginUrl}')"
               type="button" id="business-login-btn" value="Logga in"><span message key="lbl.log-in"></span>
            </a>
          </div>
          <div id="business-logged-in-user-container" ng-show="isLoggedIn">
            <div class="header-box-user-profile pull-right">
              <span class="user-name pull-right" data-ng-bind="userNameWithAccess"></span>
              <br/>
		                                <span class="user-logout pull-right">
											<a href="/saml/logout"><span message key="lbl.log-out"></span></a>
										</span>
            </div>
          </div>
        </div>
      </c:if>
    </div>
    <!-- Start mobile navigation menu -->
    <div class="collapse navbar-collapse-navigation" id="navbar-mobile-menu-national" collapse="!isCollapsed">
      <ul class="nav navbar-nav">
        <li class="divider"></li>
        <!-- National mobile menu -->
        <li class="dropdown-national">
          <a class="mobileMenuHeaderItem" data-toggle="collapse in" data-target="#national-menu" ng-click="isNationalCollapsed = !isNationalCollapsed"><span message key="nav.national-header"></span><span class="caret pull-right mobile-menu-caret"></span></a>
          <ul class="collapse" id="national-menu" collapse="!isNationalCollapsed">
            <li class="subMenuItem"><a data-ng-href="#/nationell/oversikt{{queryString}}" id="navOverviewLink" ctrlname="NationalOverviewCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.oversikt"></span></a></li>
            <li class="subMenuItem"><a data-ng-href="#/nationell/sjukfallPerManad{{queryString}}" id="navCasesPerMonthLink" ctrlname="NationalCasesPerMonthCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.sjukfall-totalt"></span></a></li>
            <li class="subMenuItem">
              <a data-toggle="collapse in" data-target="#national-dia-chapter-menu" ng-click="isNationalDiaChapterCollapsed = !isNationalDiaChapterCollapsed"><span message key="nav.mobile.trigger.diagnosgrupp-diagnoskapitel"></span><span class="caret pull-right mobile-menu-caret mobile-sub-caret"></span></a>
              <ul class="collapse" id="national-dia-chapter-menu" collapse="isNationalDiaChapterCollapsed">
                <li class="subMenuItem"><a data-ng-href="#/nationell/diagnosgrupp{{queryString}}" id="navDiagnosisGroupsLink" ctrlname="NationalDiagnosgruppCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.diagnosgrupp"></span></a></li>
                <li class="subMenuItem"><a data-ng-href="#/nationell/diagnosavsnitt{{queryString}}" id="navDiagnosisSubGroupsLink" ctrlname="NationalDiagnosavsnittCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.enskilt-diagnoskapitel"></span></a></li>
              </ul>
            </li>
            <li class="subMenuItem"><a data-ng-href="#/nationell/aldersgrupper{{queryString}}" id="navAgeGroupsLink" ctrlname="NationalAgeGroupCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.aldersgrupp"></span></a></li>
            <li class="subMenuItem"><a data-ng-href="#/nationell/sjukskrivningsgrad{{queryString}}" id="navSickLeaveDegreeLink" ctrlname="NationalDegreeOfSickLeaveCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.sjukskrivningsgrad"></span></a></li>
            <li class="subMenuItem"><a data-ng-href="#/nationell/sjukskrivningslangd{{queryString}}" id="navSickLeaveLengthLink" ctrlname="NationalSickLeaveLengthCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.sjukskrivningslangd"></span></a></li>
            <li class="subMenuItem">
              <a data-toggle="collapse in" data-target="#national-lan-kon-menu" ng-click="isNationalLanKonCollapsed = !isNationalLanKonCollapsed"><span message key="nav.mobile.trigger.lan-andel-per-kon"></span><span class="caret pull-right mobile-menu-caret mobile-sub-caret"></span></a>
              <ul class="collapse" id="national-lan-kon-menu" collapse="isNationalLanKonCollapsed">
                <li><a data-ng-href="#/nationell/lan{{queryString}}" id="navCountyLink" id="navCountyLink" ctrlname="NationalCasesPerCountyCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.lan"></span></a></li>
                <li><a data-ng-href="#/nationell/andelSjukfallPerKon{{queryString}}" id="navCasesPerSexLink" ctrlname="NationalCasesPerSexCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.lan-andel-sjukfall-per-kon"></span></a></li>
              </ul>
            </li>
          </ul>
        </li>
        <li class="divider"></li>

        <!-- Business mobile menu -->
        <li class="dropdown-business">
          <a class="mobileMenuHeaderItem" data-toggle="collapse in" data-target="#business-menu" ng-click="isBusinessCollapsed = !isBusinessCollapsed"><span message key="nav.business-header"></span><span class="caret pull-right mobile-menu-caret"></span></a>
          <ul class="collapse" id="business-menu" collapse="!isBusinessCollapsed">
            <li class="subMenuItem"><a data-ng-href="#/verksamhet/oversikt{{queryString}}" ctrlname="businessOverviewCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.oversikt"></span></a></li>
            <li class="subMenuItem" ng-show="isProcessledare || isDelprocessledare"><a data-ng-href="#/verksamhet/sjukfallperenhet{{queryString}}"
                                                                                       id="navBusinessCasesPerMonthLink" ctrlname="VerksamhetCasesPerBusinessCtrl"
                                                                                       navigationaware><span message key="nav.vardenhet"></span></a></li>
            <li class="subMenuItem"><a data-ng-href="#/verksamhet/sjukfallPerManad{{queryString}}" id="navBusinessCasesPerMonthLink" ctrlname="VerksamhetCasesPerMonthCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.sjukfall-totalt"></span></a></li>
            <li class="subMenuItem">
              <a class="dropdown-toggle subMenuItem" data-toggle="collapse in" data-target="#business-dia-chapter" ng-click="isBusinessDiaChapterCollapsed = !isBusinessDiaChapterCollapsed"><span message key="nav.mobile.trigger.diagnosgrupp-diagnoskapitel"></span><span class="caret pull-right mobile-menu-caret mobile-sub-caret"></span></a>
              <ul class="collapse" id="business-dia-chapter" collapse="isBusinessDiaChapterCollapsed">
                <li class="subMenuItem"><a data-ng-href="#/verksamhet/diagnosgrupp{{queryString}}" id="navBusinessDiagnosisGroupsLink" ctrlname="VerksamhetDiagnosgruppCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.diagnosgrupp"></span></a></li>
                <li class="subMenuItem"><a data-ng-href="#/verksamhet/diagnosavsnitt{{queryString}}" id="navBusinessDiagnosisSubGroupsLink" ctrlname="VerksamhetDiagnosavsnittCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.enskilt-diagnoskapitel"></span></a></li>
              </ul>
            </li>
            <li class="subMenuItem"><a data-ng-href="#/verksamhet/aldersgrupper{{queryString}}" id="navBusinessAgeGroupsLink" ctrlname="VerksamhetAgeGroupCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.aldersgrupp"></span></a></li>
            <li class="subMenuItem"><a data-ng-href="#/verksamhet/sjukskrivningsgrad{{queryString}}" id="navBusinessSickLeaveDegreeLink" ctrlname="VerksamhetDegreeOfSickLeaveCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.sjukskrivningsgrad"></span></a></li>
            <li class="subMenuItem">
              <a class="dropdown-toggle subMenuItem" data-toggle="collapse in" data-target="#business-sicklength-ongoing-morethan90" ng-click="isBusinessSickOn90Collapsed = !isBusinessSickOn90Collapsed"><span message key="nav.mobile.trigger.sjukskrivningslangd-pagaende-90-dagar"></span><span class="caret pull-right mobile-menu-caret mobile-sub-caret"></span></a>
              <ul class="collapse" id="business-sicklength-ongoing-morethan90" collapse="isBusinessSickOn90Collapsed">
                <li class="subMenuItem"><a data-ng-href="#/verksamhet/sjukskrivningslangd{{queryString}}" id="navBusinessSickLeaveLengthLink" ctrlname="VerksamhetSickLeaveLengthCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.sjukskrivningslangd"></span></a></li>
                <li class="subMenuItem"><a data-ng-href="#/verksamhet/langasjukskrivningar{{queryString}}" id="navBusinessMoreNinetyDaysSickLeaveLink" ctrlname="VerksamhetLongSickLeavesCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.sjukskrivningslangd-mer-an-90-dagar"></span></a></li>
              </ul>
            </li>
          </ul>
        </li>
        <li class="divider"></li>
        <!-- About mobile menu -->
        <li class="dropdown-about-statistic">
          <a class="mobileMenuHeaderItem" data-toggle="collapse in" data-target="#about-menu" ng-click="isAboutCollapsed = !isAboutCollapsed"><span message key="nav.about-header"></span><span class="caret pull-right mobile-menu-caret"></span></a>
          <ul class="collapse" id="about-menu" collapse="!isAboutCollapsed">
            <li class="subMenuItem"><a class="first-item-in-menu" data-ng-href="#/om/tjansten{{queryString}}" ctrlname="AboutServiceCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.allmant-om-tjansten"></span></a></li>
            <li class="subMenuItem"><a data-ng-href="#/om/inloggning{{queryString}}" ctrlname="AboutLoginCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.inloggning-behorighet"></span></a></li>
            <li class="subMenuItem"><a data-ng-href="#/om/vanligafragor{{queryString}}" ctrlname="AboutFaqCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.faq"></span></a></li>
            <li class="subMenuItem"><a data-ng-href="#/om/kontakt{{queryString}}" ctrlname="AboutContactCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware><span message key="nav.kontakt-support"></span></a></li>
          </ul>
        </li>
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
<!-- MOBILE NAVIGATION END -->
