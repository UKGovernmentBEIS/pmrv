package uk.gov.pmrv.api.authorization.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.UserRoleType;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.pmrv.api.authorization.core.repository.UserRoleTypeRepository;
import uk.gov.pmrv.api.authorization.core.transform.UserRoleTypeMapper;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRoleTypeServiceTest {

    private static final String USER_ID = "user_id";

    @InjectMocks
    private UserRoleTypeService userRoleTypeService;

    @Mock
    private UserRoleTypeRepository userRoleTypeRepository;

    @Mock
    private UserRoleTypeMapper userRoleTypeMapper;

    @Test
    void getRoleTypeByUserId() {
        UserRoleType userRoleType = UserRoleType.builder().userId(USER_ID).roleType(RoleType.OPERATOR).build();
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(USER_ID).roleType(RoleType.OPERATOR).build();

        when(userRoleTypeRepository.findById(USER_ID)).thenReturn(Optional.of(userRoleType));
        when(userRoleTypeMapper.toUserRoleTypeDTO(userRoleType)).thenReturn(userRoleTypeDTO);

        UserRoleTypeDTO result = userRoleTypeService.getUserRoleTypeByUserId(USER_ID);

        verify(userRoleTypeRepository, times(1)).findById(USER_ID);
        verify(userRoleTypeMapper, times(1)).toUserRoleTypeDTO(userRoleType);

        assertEquals(userRoleTypeDTO, result);
    }

    @Test
    void getRoleTypeByUserIdWhenNoAuthoritiesExist() {
        UserRoleType userRoleType = UserRoleType.builder().userId(USER_ID).roleType(RoleType.OPERATOR).build();
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(USER_ID).roleType(RoleType.OPERATOR).build();

        when(userRoleTypeRepository.findById(USER_ID)).thenReturn(Optional.empty());
        when(userRoleTypeMapper.toUserRoleTypeDTO(userRoleType)).thenReturn(userRoleTypeDTO);

        UserRoleTypeDTO actualDTO = userRoleTypeService.getUserRoleTypeByUserId(USER_ID);

        assertEquals(userRoleTypeDTO, actualDTO);
    }

    @Test
    void isUserOperator() {
        UserRoleType userRoleType = UserRoleType.builder().userId(USER_ID).roleType(RoleType.OPERATOR).build();
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(USER_ID).roleType(RoleType.OPERATOR).build();

        when(userRoleTypeRepository.findById(USER_ID)).thenReturn(Optional.of(userRoleType));
        when(userRoleTypeMapper.toUserRoleTypeDTO(userRoleType)).thenReturn(userRoleTypeDTO);

        assertTrue(userRoleTypeService.isUserOperator(USER_ID));
    }

    @Test
    void isUserRegulator() {
        UserRoleType userRoleType = UserRoleType.builder().userId(USER_ID).roleType(RoleType.REGULATOR).build();
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(USER_ID).roleType(RoleType.REGULATOR).build();

        when(userRoleTypeRepository.findById(USER_ID)).thenReturn(Optional.of(userRoleType));
        when(userRoleTypeMapper.toUserRoleTypeDTO(userRoleType)).thenReturn(userRoleTypeDTO);

        assertTrue(userRoleTypeService.isUserRegulator(USER_ID));
    }

    @Test
    void isUserVerifier() {
        UserRoleType userRoleType = UserRoleType.builder().userId(USER_ID).roleType(RoleType.VERIFIER).build();
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(USER_ID).roleType(RoleType.VERIFIER).build();

        when(userRoleTypeRepository.findById(USER_ID)).thenReturn(Optional.of(userRoleType));
        when(userRoleTypeMapper.toUserRoleTypeDTO(userRoleType)).thenReturn(userRoleTypeDTO);

        assertTrue(userRoleTypeService.isUserVerifier(USER_ID));
    }

}