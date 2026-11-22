package ru.otus.java.pro.homeworks.homework14.web;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.java.pro.homeworks.homework14.service.ClientService;

@Controller
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @InitBinder("clientForm")
    void bindFormFields(WebDataBinder binder) {
        binder.setAllowedFields("name", "street", "phones");
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/clients";
    }

    @GetMapping("/clients")
    public String list(Model model) {
        model.addAttribute("clients", clientService.findAll());

        return "clients/list";
    }

    @GetMapping("/clients/new")
    public String newClient(Model model) {
        model.addAttribute("clientForm", new ClientForm());

        return form(model, null);
    }

    @PostMapping("/clients")
    public String create(@Valid @ModelAttribute("clientForm") ClientForm clientForm, BindingResult errors, Model model, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        if (!errors.hasErrors()) {
            try {
                clientService.create(clientForm.getName(), clientForm.getStreet(), clientForm.getPhones());
            } catch (IllegalArgumentException e) {
                errors.reject("client.invalid", e.getMessage());
            }
        }

        if (errors.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return form(model, null);
        }

        redirectAttributes.addFlashAttribute("message", "Клиент успешно создан.");
        return "redirect:/clients";
    }

    @GetMapping("/clients/{id}")
    public String detail(@PathVariable long id, Model model) {
        model.addAttribute("client", clientService.getById(id));

        return "clients/detail";
    }

    @GetMapping("/clients/{id}/edit")
    public String edit(@PathVariable long id, Model model) {
        model.addAttribute("clientForm", ClientForm.from(clientService.getById(id)));

        return form(model, id);
    }

    @PostMapping("/clients/{id}")
    public String update(@PathVariable long id, @Valid @ModelAttribute("clientForm") ClientForm clientForm, BindingResult errors, Model model, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        clientService.getById(id);

        if (!errors.hasErrors()) {
            try {
                clientService.update(id, clientForm.getName(), clientForm.getStreet(), clientForm.getPhones());
            } catch (IllegalArgumentException e) {
                errors.reject("client.invalid", e.getMessage());
            }
        }

        if (errors.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return form(model, id);
        }

        redirectAttributes.addFlashAttribute("message", "Изменения сохранены.");

        return "redirect:/clients";
    }

    @GetMapping("/clients/{id}/delete")
    public String confirmDelete(@PathVariable long id, Model model) {
        model.addAttribute("client", clientService.getById(id));

        return "clients/delete";
    }

    @PostMapping("/clients/{id}/delete")
    public String delete(@PathVariable long id, RedirectAttributes redirectAttributes) {
        clientService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Клиент удалён.");

        return "redirect:/clients";
    }

    private String form(Model model, Long clientId) {
        model.addAttribute("clientId", clientId);
        model.addAttribute("editing", clientId != null);

        return "clients/form";
    }
}