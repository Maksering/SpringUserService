package springApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import springApp.dto.UserDTO;
import springApp.entity.User;
import springApp.mapper.UserMapper;
import springApp.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldGetAllUsers() {
        User user = new User();
        UserDTO dto = new UserDTO();
        dto.setId(1);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.mapToUserDTO(user)).thenReturn(dto);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        verify(userRepository).findAll();
        verify(userMapper).mapToUserDTO(user);
    }

    @Test
    void shouldGetUserById() {
        User user = new User();
        user.setId(1);
        UserDTO dto = new UserDTO();
        dto.setId(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToUserDTO(user)).thenReturn(dto);

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(userRepository).findById(1L);
        verify(userMapper).mapToUserDTO(user);
    }

    @Test
    void whenGetUserByIdUserNotFound_ShouldThrowException() {
        when(userRepository.findById(-1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUserById(-1L));

        assertEquals("User not found by id: " + -1L, exception.getMessage());
        verify(userRepository).findById(-1L);
    }

    @Test
    void shouldCreateAndReturnUser() {
        User user = new User();
        user.setId(1);
        UserDTO inputDTO = new UserDTO();
        inputDTO.setId(1);

        when(userMapper.mapToUserEntity(any())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.mapToUserDTO(any())).thenReturn(inputDTO);

        UserDTO result = userService.createUser(inputDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(userRepository).save(user);
    }

    @Test
    void shouldUpdateExistingUser() {
        User existUser = new User();
        existUser.setId(1);
        existUser.setName("OldName");
        UserDTO updateDTO = new UserDTO();
        updateDTO.setId(1);
        updateDTO.setName("UpdateName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));
        when(userRepository.save(any())).thenReturn(existUser);
        when(userMapper.mapToUserDTO(any())).thenReturn(updateDTO);

        UserDTO result = userService.updateUser(1L, updateDTO);

        assertNotNull(result);
        assertEquals("UpdateName", result.getName());
    }

    @Test
    void whenUpdateUserUserNotfound_ShouldThrowException() {
        when(userRepository.findById(-1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUser(-1L, new UserDTO()));

        assertEquals("User not found by id: " + -1L, exception.getMessage());
        verify(userRepository).findById(-1L);
        verify(userMapper, never()).updateUserFromDTO(any(), any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).mapToUserDTO(any());
    }

    @Test
    void shouldDeleteExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void whenDeleteNotExistingUser_ShouldThrowException() {
        when(userRepository.existsById(-1L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.deleteUser(-1L));
    }
}
