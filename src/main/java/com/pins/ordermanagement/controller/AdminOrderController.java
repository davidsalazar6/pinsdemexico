package com.pins.ordermanagement.controller;

import com.pins.ordermanagement.model.Order;
import com.pins.ordermanagement.model.Role;
import com.pins.ordermanagement.model.User;
import com.pins.ordermanagement.security.AuthSuccessHandler;
import com.pins.ordermanagement.security.MyUserPrincipal;
import com.pins.ordermanagement.service.*;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthSuccessHandler authSuccessHandler;


    /*@ResponseBody*/
/*    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("orders",orderService.getAll());
        return "index";
    }*/

    @GetMapping("/")
    public String index(Model model,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "size", required = false, defaultValue = "5") int pageSize){
        if(orderNumber!=null && !orderNumber.equals("")){

            List<Order> orderList = orderService.findByOrderNumber(orderNumber.trim());
            model.addAttribute("orders", orderList);
            model.addAttribute("pages", new int[1]);
        }
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("orderNumber", orderNumber);
        return "landing";
    }

    @GetMapping("/admin")
    public String adminLogin(Model model,
                        @RequestParam(value = "orderNumber", defaultValue = "") String orderNumber,
                        @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber,
                        @RequestParam(value = "size", required = false, defaultValue = "5") int pageSize){
        Page<Order> orderPage = orderService.findByOrderNumberContains(orderNumber.trim(),pageNumber,pageSize);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("pages", new int[orderPage.getTotalPages()]);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("orderNumber", orderNumber);
        return "index";
    }

    @GetMapping("/create")
    public String createOrder(Model model){
        Order order = new Order();
        order.setOrderNumber(orderService.generateRandomOrderNumber());
        model.addAttribute("order",order);
        return "order-form";
    }



    @PostMapping("/save")
    public String saveOrder(@Validated @ModelAttribute("order") Order order, BindingResult result){
        if(result.hasErrors()){
            return "order-form";
        }
        orderService.save(order);
        return "redirect:/admin";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") long id, Model model){
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        return "order-form";
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id, RedirectAttributes attributes){
        orderService.deleteById(id);
        attributes.addFlashAttribute("success", "The order has been deleted!");
        return "redirect:/admin";
    }

    @GetMapping("/login")
    public String login(Model model,@RequestParam(value = "error",required = false) String error){
        String redirectUrl = authSuccessHandler.redirectIfAuthenticated();
        if(redirectUrl!=null){
            return redirectUrl;
        }
        if(error!=null){
            model.addAttribute("message", "Bad credentials");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public ModelAndView saveUser(ModelAndView modelAndView , User user){
        /*if(result.hasErrors()){
            modelAndView.setViewName("signup");
            return modelAndView;
        }*/
        User existingUser = userService.getUserByEmail(user.getEmail());
        if(existingUser!=null){
            modelAndView.addObject("message", "User account with email already exists!");
            modelAndView.setViewName("signup");
        }else {
            List<Role> roleList = roleService.getByRoleName("USER");
            user.setRoles(roleList);
            userService.saveUser(user);
            //modelAndView.addObject("emailId",user.getEmail());
            modelAndView.addObject("success", "Signed up Successfully!");
            modelAndView.setViewName("signup");
        }
        return modelAndView;
    }

    @GetMapping("/403")
    public String accessDenied(){
        return "accessDenied";
    }


    @GetMapping("/sssdownload-pdf")
    public void downloadPdf(HttpServletResponse response){
  /*      try {
            Path file = Paths.get(pdfService.generatePdf().getAbsolutePath());
            if(Files.exists(file)){
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @RequestMapping(value = "/download-pdf", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> pdfReport() {

        MyUserPrincipal myUserPrincipal = authSuccessHandler.getLoginInUserPrincipal();

        if(myUserPrincipal==null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        String role = myUserPrincipal.getAuthorities().toString();
        List<Order> orderList = new ArrayList<>();

        if(role.contains("ADMIN")){
            orderList = orderService.getAll();
        }else if(role.contains("USER")){
            orderList = orderService.findOrderByCustomerEmail(myUserPrincipal.getUsername());
        }

        ByteArrayInputStream bis = GeneratePdfReport.pdfReport(orderList);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
