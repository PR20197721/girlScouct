<%@include file="/libs/foundation/global.jsp" %>
<!-- body -->
<body>
<div class="off-canvas-wrap">

  <main class="inner-wrap">
    <nav class="tab-bar">
        <section class="left-small">
          <a class="left-off-canvas-toggle menu-icon" ><span></span></a>
        </section>

        <section class="middle tab-bar-section">
          <h1 class="title">Foundation</h1>
        </section>

        <section class="right-small">
          <a class="right-off-canvas-toggle menu-icon" ><span></span></a>
        </section>
    </nav>
    <!-- Off Canvas Menu -->
    <aside class="right-off-canvas-menu">
      <ul class="off-canvas-list">
        <li><label>Users</label></li>
        <li><a href="#">Hari Seldon</a></li>
        <li class="has-submenu"><a href="#">R. Giskard Reventlov</a>
            <ul class="right-submenu">
                <li class="back"><a href="#">Back</a></li>
                <li><label>Level 1</label></li>
                <li><a href="#">Link 1</a></li>
                <li class="has-submenu"><a href="#">Link 2 w/ submenu</a>
                    <ul class="right-submenu">
                        <li class="back"><a href="#">Back</a></li>
                        <li><label>Level 2</label></li>
                        <li><a href="#">...</a></li>
                    </ul>
                </li>
                <li><a href="#">...</a></li>
            </ul>
        </li>
        <li><a href="#">...</a></li>
      </ul>
    </aside>
    <section class="main-section">
        <div class="header row">
          <cq:include script="header.jsp"/>
        </div>
        <div class="content row">
          <cq:include script="content.jsp"/>
        </div>
        <footer class="row">
          <cq:include script="footer.jsp"/>
        </footer>
        <cq:include script="bodylibs.jsp"/>
    </section>
    <!-- close the off-canvas menu -->
     <a class="exit-off-canvas"></a>
  </main>
</div>
</body>
<!-- END of body -->
