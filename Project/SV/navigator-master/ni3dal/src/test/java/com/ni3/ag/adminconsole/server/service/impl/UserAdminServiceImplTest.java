package com.ni3.ag.adminconsole.server.service.impl;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.validation.ACException;
import junit.framework.TestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserAdminServiceImplTest extends TestCase {

    public void testCopyGroup() throws ACException, CloneNotSupportedException {
        final Group sourceGroup = mock(Group.class);
        final Group clonedGroup = mock(Group.class);
        when(sourceGroup.cloneDeep("groupCopy")).thenReturn(clonedGroup);
        final GroupScope groupScope = mock(GroupScope.class);
        when(sourceGroup.getGroupScope()).thenReturn(groupScope);
        
        final GroupDAO groupDAO = mock(GroupDAO.class);
        when(groupDAO.getGroup(sourceGroup.getId())).thenReturn(sourceGroup);
        when(groupDAO.addGroup(any(Group.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Group group = (Group) invocationOnMock.getArguments()[0];
                group.setId(2);
                return group;
            }
        });
        
        final UserAdminServiceImpl service = mock(UserAdminServiceImpl.class);
        when(service.getGroupDAO()).thenReturn(groupDAO);
        
        when(service.copyGroup(sourceGroup, "groupCopy")).thenCallRealMethod();

        Group actualGroup = service.copyGroup(sourceGroup, "groupCopy");

        assertEquals(clonedGroup, actualGroup);
        verify(sourceGroup).cloneDeep("groupCopy");
        verify(groupDAO).addGroup(clonedGroup);
        verify(groupScope).cloneFor(clonedGroup);
    }

}
