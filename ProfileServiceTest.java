package com.app.profile;

import com.app.department.DepartmentService;
import com.app.department.DepartmentRepository;
import com.app.department.Department;

import com.app.system.exception.ObjectNotFoundException;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public// Используем Mockito
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository; // Мок репозитория

    @Mock
    private DepartmentService departmentService; // Мок сервиса департаментов

    @InjectMocks
    private ProfileService profileService; // Тестируемый сервис

    @Test
    public void testUpdateDepartment_Success() {
        // Данные для теста
        String profileId = "1";
        String departmentId = "10";

        Profile mockProfile = new Profile();
        mockProfile.setId(1L);
        mockProfile.setName("John Doe");

        Department mockDepartment = new Department();
        mockDepartment.setId(10L);
        mockDepartment.setName("IT Department");

        // Настройка поведения моков
        when(profileRepository.findById(1L)).thenReturn(Optional.of(mockProfile));
        when(departmentService.find(departmentId)).thenReturn(mockDepartment);
        when(profileRepository.save(mockProfile)).thenReturn(mockProfile);

        // Вызов тестируемого метода
        Profile updatedProfile = profileService.updateDepartment(profileId, departmentId);

        // Проверки
        assertNotNull(updatedProfile);
        assertEquals(mockDepartment, updatedProfile.getDepartment());
        verify(profileRepository, times(1)).findById(1L);
        verify(departmentService, times(1)).find(departmentId);
        verify(profileRepository, times(1)).save(mockProfile);
    }

    @Test
    public void testUpdateDepartment_ProfileNotFound() {
        // Данные для теста
        String profileId = "1";
        String departmentId = "10";

        // Настройка поведения моков
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        // Проверка выброса исключения
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> profileService.updateDepartment(profileId, departmentId)
        );

        assertEquals("Не найден профиль по ИД: 1", exception.getMessage());
        verify(profileRepository, times(1)).findById(1L);
        verify(departmentService, never()).find(anyString());
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    public void testUpdateDepartment_DepartmentNotFound() {
        // Данные для теста
        String profileId = "1";
        String departmentId = "10";

        Profile mockProfile = new Profile();
        mockProfile.setId(1L);
        mockProfile.setName("John Doe");

        // Настройка поведения моков
        when(profileRepository.findById(1L)).thenReturn(Optional.of(mockProfile));
        when(departmentService.find(departmentId)).thenThrow(new ObjectNotFoundException("Не найден департамент по ИД: 10"));

        // Проверка выброса исключения
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> profileService.updateDepartment(profileId, departmentId)
        );

        assertEquals("Не найден департамент по ИД: 10", exception.getMessage());
        verify(profileRepository, times(1)).findById(1L);
        verify(departmentService, times(1)).find(departmentId);
        verify(profileRepository, never()).save(any(Profile.class));
    }
}
