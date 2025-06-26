package springApp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springApp.dto.UserDTO;
import springApp.service.KafkaProducerService;
import springApp.service.UserService;


@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;


    public UserController(UserService userService, KafkaProducerService producerService) {
        this.userService = userService;
    }
    @GetMapping
    public String getAllUsers(Model model) {
        logger.info("Request for all users");
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        logger.info("Request for user create form");
        model.addAttribute("userDTO", new UserDTO());
        return "users/create";
    }

    @PostMapping
    public String createUser(@ModelAttribute UserDTO userDto, RedirectAttributes redirectAttributes) {
        logger.info("Try to create new user");
        try {
            userService.createUser(userDto);
            logger.info("User created with ID: " + userDto.getId());
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
        } catch (Exception e) {
            logger.error("Error on user create: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/{id}/update")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("Request for showing edit form for userID: " + id);
        model.addAttribute("userDto", userService.getUserById(id));
        return "users/update";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute UserDTO userDto,
                             RedirectAttributes redirectAttributes) {
        logger.info("Try to update with id: " + id);
        try {
            userService.updateUser(id, userDto);
            logger.info("Updating user with id: " + id);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        } catch (Exception e) {
            logger.error("Error on user update, userid: " + id + " Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Trying to delete user, userid: " + id);
        try {
            String email = userService.getUserById(id).getEmail();
            userService.deleteUser(id);
            logger.info("Deleting user with id: " + id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            logger.error("Error on user delete, userid: " + id + " Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }
}
