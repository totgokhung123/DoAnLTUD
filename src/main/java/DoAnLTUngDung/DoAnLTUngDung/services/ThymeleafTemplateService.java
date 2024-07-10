//package DoAnLTUngDung.DoAnLTUngDung.services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.context.Context;
//import org.thymeleaf.spring5.SpringTemplateEngine;
//
//import java.util.Map;
//
//@Service
//public class ThymeleafTemplateService {
//
//    @Autowired
//    private SpringTemplateEngine templateEngine;
//
//    public String processTemplate(String templateName, Map<String, Object> variables) {
//        Context context = new Context();
//        context.setVariables(variables);
//        return templateEngine.process(templateName, context);
//    }
//}
