package com.dal.skillswap.service.impl;

import com.dal.skillswap.mapper.UserMapper;
import com.dal.skillswap.mapper.UserSkillMapper;
import com.dal.skillswap.repository.UserRepository;
import com.dal.skillswap.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.dal.skillswap.constants.PathConstants.PROFILE_IMAGES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class FileStoreServiceImplTests {

    @InjectMocks
    private FileStoreServiceImpl fileStoreService;

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Spy
    private UserSkillMapper userSkillMapper = Mappers.getMapper(UserSkillMapper.class);

    private final Path rootLocation = Paths.get(PROFILE_IMAGES);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testStoreFile() {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        when(userService.getProfilePicture()).thenReturn("TestFile");
        when(userService.setProfilePicture(any())).thenReturn(true);

        assertTrue(fileStoreService.store(file, PROFILE_IMAGES).contains(".txt"));
    }

    @Test
    void testStoreEmptyFile() {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );
        when(userService.getProfilePicture()).thenReturn("TestFile");
        when(userService.setProfilePicture(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> fileStoreService.store(file, PROFILE_IMAGES));
    }

    @Test
    void testStoreFileIOException() throws IOException {
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getInputStream()).thenThrow(IOException.class);
        when(userService.getProfilePicture()).thenReturn("TestFile");
        when(userService.setProfilePicture(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> fileStoreService.store(file, PROFILE_IMAGES));
    }

    @Test
    public void testLoadNonExistingFile() {
        String filename = "nonexistent.txt";
        assertThrows(RuntimeException.class, () -> fileStoreService.load(filename, PROFILE_IMAGES),
                "Could not read the file!");
    }

    @Test
    public void testDeleteFile() {
        assertFalse(fileStoreService.delete("test", PROFILE_IMAGES));
    }

}
