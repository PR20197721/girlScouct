
<%@ page import="java.util.*, org.apache.shiro.*,
org.apache.shiro.SecurityUtils,
 org.apache.shiro.authc.*,
org.apache.shiro.config.IniSecurityManagerFactory,
 org.apache.shiro.mgt.SecurityManager,
 org.apache.shiro.session.Session,
 org.apache.shiro.subject.Subject,
 org.apache.shiro.util.Factory,
 org.slf4j.Logger,
 org.slf4j.LoggerFactory
" %>

<%


		Factory<SecurityManager> factory = new IniSecurityManagerFactory("/Users/akobovich/Desktop/shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);


        // get the currently executing user:
        Subject currentUser = SecurityUtils.getSubject();

        // Do some stuff with a Session (no need for a web or EJB container!!!)
        Session sessionX = currentUser.getSession();
        sessionX.setAttribute("someKey", "aValue");
        String value = (String) sessionX.getAttribute("someKey");
        if (value.equals("aValue")) {
            out.println("<br/>Retrieved the correct value! [" + value + "]");
        }

        // let's login the current user so we can check against roles and permissions:
        if (!currentUser.isAuthenticated()) {
            //UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
             UsernamePasswordToken token = new UsernamePasswordToken("root", "secret");
            token.setRememberMe(true);
            try {
                currentUser.login(token);
            } catch (UnknownAccountException uae) {
            	out.println("<br/>There is no user with username of " + token.getPrincipal());
            } catch (IncorrectCredentialsException ice) {
            	out.println("<br/>Password for account " + token.getPrincipal() + " was incorrect!");
            } catch (LockedAccountException lae) {
            	out.println("<br/>The account for username " + token.getPrincipal() + " is locked.  " +
                        "Please contact your administrator to unlock it.");
            }
            // ... catch more exceptions here (maybe custom ones specific to your application?
            catch (AuthenticationException ae) {
                //unexpected condition?  error?
            }
        }

        //say who they are:
        //print their identifying principal (in this case, a username):
        out.println("<br/>User [" + currentUser.getPrincipal() + "] logged in successfully.");

        //test a role:
        if (currentUser.hasRole("schwartz")) {
        	out.println("<br/>May the Schwartz be with you!");
        } else {
        	out.println("<br/>Hello, mere mortal.");
        }

        //test a typed permission (not instance-level)
        if (currentUser.isPermitted("lightsaber:weild")) {
        	out.println("<br/>You may use a lightsaber ring.  Use it wisely.");
        } else {
        	out.println("<br/>Sorry, lightsaber rings are for schwartz masters only.");
        }

        //a (very powerful) Instance Level permission:
        if (currentUser.isPermitted("winnebago:drive:eagle5")) {
        	out.println("<br/>You are permitted to 'drive' the winnebago with license plate (id) 'eagle5'.  " +
                    "Here are the keys - have fun!");
        } else {
        	out.println("<br/>Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
        }

        //all done - log out!
        currentUser.logout();
%>