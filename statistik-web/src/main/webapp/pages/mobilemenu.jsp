<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- MOBILE NAVIGATION START -->
<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header navbar-header-margins">
      <span class="navbar-brand pull-left"><span message key="lbl.mobile-menu"></span></span>
      <button type="button" class="navbar-toggle collapsed pull-left" data-toggle="collapse in" data-target="#navbar-mobile-menu-national" ng-click="isCollapsed = !isCollapsed">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <c:if test="${param.loginVisible}">
        <div class="col-xs-6 hidden-sm hidden-md hidden-lg pull-right">
          <div class="pull-right" ng-hide="isLoggedIn">
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
    <div class="collapse navbar-collapse-navigation" id="navbar-mobile-menu-national" collapse="isCollapsed">
      <ul class="nav navbar-nav" style="margin-top: 0">

        <li class="divider" data-ng-repeat-start="menu in menus"  data-ng-if="showMenu(menu, true)"></li>
        <li data-ng-repeat-end  data-ng-if="showMenu(menu, true)">
          <a class="mobileMenuHeaderItem" data-ng-click="toggleMenu(menu)">
            <span message key="{{menu.name}}"></span><span class="caret pull-right mobile-menu-caret"></span>
          </a>

          <ul data-ng-if="menu.show">
            <li class="subMenuItem"  data-ng-repeat="submenu in menu.subMenu" data-ng-if="showMenu(submenu)">
              <a data-ng-href="{{submenu.link}}{{queryString}}"
                 data-ng-click="toggleMobileMenu()"
                 data-ng-class="{'not-active': !menuEnabled(submenu)}"
                 ctrlname="{{submenu.ctrl}}"
                 role="menuitem" navigationaware><span message key="{{submenu.name}}"></span></a>

              <ul data-ng-if="submenu.subMenu && showMenu(submenu)">
                <li class="subMenuItem" data-ng-repeat="subsubmenu in submenu.subMenu">
                  <a data-ng-href="{{subsubmenu.link}}{{queryString}}" id="{{subsubmenu.id}}" ctrlname="{{subsubmenu.ctrl}}" role="menuitem"
                     data-ng-click="toggleMobileMenu()" navigationaware><span message key="{{subsubmenu.name}}"></span></a></li>
              </ul>
            </li>
          </ul>
        </li>
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
<!-- MOBILE NAVIGATION END -->
