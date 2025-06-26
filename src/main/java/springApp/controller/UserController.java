package springApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "User Controller",
        description = "Контроллер для управления пользователями"
)
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final KafkaProducerService producerService;

    public UserController(UserService userService, KafkaProducerService producerService) {
        this.userService = userService;
        this.producerService = producerService;
    }
    @Operation(
            summary = "Вывести всех пользователей при открытии страницы",
            description = "Возвращает HTML-странцу со всеми пользователями"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Страницу со списком пользователей успешно загруженна"
    )
    @GetMapping
    public String getAllUsers(Model model) {
        logger.info("Request for all users");
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @Operation(
            summary = "Открыть HTML-странцу для создания пользователя",
            description = "Возвращает HTML-странцу для создания нового пользователя"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Страницу для создания нового пользователя успешно загруженна"
    )
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        logger.info("Request for user create form");
        model.addAttribute("userDTO", new UserDTO());
        return "users/create";
    }

    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя. " +
                    "После успешного создания перенаправляет на списко пользователей"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешное создание пользователя"
    )
    @PostMapping
    public String createUser(
            @Parameter(description = "Данные для создания пользователя")
            @ModelAttribute UserDTO userDto,

            RedirectAttributes redirectAttributes
    ) {
        logger.info("Try to create new user");
        try {
            userService.createUser(userDto);
            logger.info("User created with ID: " + userDto.getId());
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
            producerService.sendUserCreate(userDto.getEmail());
        } catch (Exception e) {
            logger.error("Error on user create: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @Operation(
            summary = "Открыть HTML-странцу для обновления данных пользователя",
            description = "Возвращает HTML-страницу для обновления данных пользователя"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Форма редактирования успешно загружена"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Пользователь не найден на сервере"
            )
    })
    @GetMapping("/{id}/update")
    public String showEditForm(
            @Parameter(
                    description = "ID пользователя",
                    example = "1",
                    required = true)
            @PathVariable Long id,

            Model model
    ){
        logger.info("Request for showing edit form for userID: " + id);
        model.addAttribute("userDto", userService.getUserById(id));
        return "users/update";
    }

    @Operation(
            summary = "Обновление пользователя",
            description = "Обновление уже существующего пользователя " +
                    "и перенаправление на список пользователей")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное обновление пользователя"
    )
    @PostMapping("/{id}")
    public String updateUser(
            @Parameter(
                    description = "ID пользователя",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные пользователя")
            @ModelAttribute UserDTO userDto,

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

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователяи и перенаправляет на список пользователей")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное обновление пользователя"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Пользователь не найден на сервере"
            )
    })
    @PostMapping("/{id}/delete")
    public String deleteUser(
            @Parameter(
                    description = "ID пользователя",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            RedirectAttributes redirectAttributes) {
        logger.info("Trying to delete user, userid: " + id);
        try {
            String email = userService.getUserById(id).getEmail();
            userService.deleteUser(id);
            logger.info("Deleting user with id: " + id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
            producerService.sendUserDelete(email);
        } catch (Exception e) {
            logger.error("Error on user delete, userid: " + id + " Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }
}
