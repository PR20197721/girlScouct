package com.gs.vtk;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.ModelMap;

@Controller
public class HelloController{
 
   @RequestMapping("/hello")
   public String printHello(ModelMap model) {
	  System.err.println("test123");
      model.addAttribute("message", "Hello Spring MVC Framework!");
      model.addAttribute("product", new Product() );
      return "hello";
   }
   
   
   /*
   @Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
		HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView("HelloWorldPage");
		model.addObject("msg", "hello world");
		
		return model;
	}
*/
}