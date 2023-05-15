package Servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import Entity.ChatMessage;
import Entity.ChatUser;

public class NewMessageServlet extends ChatServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse
            response) throws ServletException, IOException {
// По умолчанию используется кодировка ISO-8859. Так как мы
// передаѐм данные в кодировке UTF-8
// то необходимо установить соответствующую кодировку HTTP-запроса
        request.setCharacterEncoding("UTF-8");
// Извлечь из HTTP-запроса параметр 'message'
        String message = (String)request.getParameter("message");
// Если сообщение не пустое, то
        if (message!=null && !"".equals(message)) {
// По имени из сессии получить ссылку на объект ChatUser
            ChatUser author = activeUsers.get((String)
                    request.getSession().getAttribute("name"));
            author.SetTimeOut1(true);
            author.SetTimeOut2(true);
            author.SetTimeOut3(true);
            author.setLastSendMsg(Calendar.getInstance().getTimeInMillis());
            
//            activeUsers.forEach(
//            		(key, value)
//            		-> messages.add(new ChatMessage("Penetration begins", value,
//                            Calendar.getInstance().getTimeInMillis())));
            synchronized (messages) {
// Добавить в список сообщений новое
            	
                messages.add(new ChatMessage(message, author,
                        Calendar.getInstance().getTimeInMillis()));
                
            }
        }
        Iterator it = (Iterator) activeUsers.entrySet().iterator();
        while (it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        ChatUser aUser = (ChatUser) pair.getValue();
        if(Calendar.getInstance().getTimeInMillis() - aUser.getLastSendMsg() > 1000 * 60 * 1) {
        	System.out.println(pair.getKey() + " : " + String.valueOf(Calendar.getInstance().getTimeInMillis() - aUser.getLastSendMsg()));
        	messages.add(new ChatMessage((String) pair.getKey() + ", You'll be kick immideatly!", 
        			new ChatUser("System", Calendar.getInstance().getTimeInMillis(), request.getSession().getId()), Calendar.getInstance().getTimeInMillis()));
        	//activeUsers.remove(pair.getKey());
        	RequestDispatcher rd = request.getRequestDispatcher("logout.do");
        	rd.forward(request, response);
        	//response.sendRedirect("/mychat/logout.do");
        	continue;}}
// Перенаправить пользователя на страницу с формой сообщения
        response.sendRedirect("/mychat/compose_message.htm");
        
        
    }
}