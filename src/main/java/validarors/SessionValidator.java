package validarors;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;

public class SessionValidator {
    private static final Logger log = Logger.getLogger(SessionValidator.class);
    public boolean isAuthorized(HttpSession session){
        boolean check=false;
        if (session!=null &&session.getAttribute("authorized")!=null &&(boolean)session.getAttribute("authorized")){
            if (session.getAttribute("user")!=null) {
                check = true;
            } else {
                log.error("К авторизованной сессии не прикреплён user");
                session.invalidate();
            }
        }
        return check;
    }
}
