package com.example.pensionat2.controller;



import com.example.pensionat2.dto.CustomerDTO;
import com.example.pensionat2.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import java.util.logging.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customers/list";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        return "customers/form";
    }
    @GetMapping("/form")
    public String showCustomerForm(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        return "customers/form";
    }


    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute("customer") CustomerDTO customerDTO) {
        customerService.save(customerDTO);
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        CustomerDTO customer = customerService.findById(id);
        model.addAttribute("customer", customer);
        return "customers/form";
    }

    /*@GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.delete(id);
        return "redirect:/customers";
    }*/
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirect) {
        boolean success = customerService.delete(id);

        if (success) {
            logger.info("Kund med ID {} har tagits bort.", id);
            redirect.addFlashAttribute("message", "Kunden är borttagen.");
        } else {
            logger.warn("Kund med ID {} kunde inte tas bort. Kunden har bokningar.", id);
            redirect.addFlashAttribute("error", "Kunden kunde inte tas bort. Bokad rum.");
        }

        return "redirect:/customers";
    }

}
