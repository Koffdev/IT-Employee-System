package com.app.profile;

import com.app.department.DepartmentService;
import com.app.department.DepartmentRepository;
import com.app.department.Department;

import com.app.system.exception.ObjectNotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
public class ProfileServiceIntegrationTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    public void testUpdateDepartment_Success() {
        // Создаем департамент
        Department department = new Department();
        department.setName("IT Department");
        departmentRepository.save(department);

        // Создаем профиль
        Profile profile = new Profile();
        profile.setName("John Doe");
        profile.setDepartment(null); // У профиля нет департамента изначально
        profileRepository.save(profile);

        // Выполняем обновление департамента
        Profile updatedProfile = profileService.updateDepartment(profile.getId().toString(), department.getId().toString());

        // Проверяем, что департамент профиля был обновлен
        Assertions.assertNotNull(updatedProfile.getDepartment());
        Assertions.assertEquals("IT Department", updatedProfile.getDepartment().getName());

        // Проверяем, что изменения сохранены в базе данных
        Profile savedProfile = profileRepository.findById(profile.getId()).orElseThrow();
        Assertions.assertEquals(department.getId(), savedProfile.getDepartment().getId());
    }

    @Test
    public void testUpdateDepartment_DepartmentNotFound() {
        // Создаем профиль
        Profile profile = new Profile();
        profile.setName("Jane Doe");
        profileRepository.save(profile);

        // Пытаемся обновить департамент с несуществующим ID
        String invalidDepartmentId = "999";

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> profileService.updateDepartment(profile.getId().toString(), invalidDepartmentId));

        Assertions.assertEquals("Department not found with ID: " + invalidDepartmentId, exception.getMessage());
    }
}

