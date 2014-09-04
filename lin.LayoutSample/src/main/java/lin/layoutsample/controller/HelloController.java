package lin.layoutsample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/hello")
public class HelloController {
	private Logger logger = LoggerFactory.getLogger(HelloController.class);
	
	@RequestMapping("/say-hi")
	public ModelAndView sayHi(){
		logger.debug("HelloController:sayHi");
		ModelAndView mv = new ModelAndView("WEB-INF/views/say-hi");
		return mv;
	}
}
