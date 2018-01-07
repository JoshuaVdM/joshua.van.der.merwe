package com.jvdm.recruits;

import com.jvdm.recruits.Model.Permission;
import com.jvdm.recruits.Model.Recruit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RecruitTest {
    @Test
    public void recruitValidator_CorrectPermissions() {
        Permission recruitPermissions = new Permission();
        recruitPermissions.setAdmin(true);

        Recruit testRecruit = new Recruit();
        testRecruit.setPermissions(recruitPermissions);
        assertThat(testRecruit.getPermissions().isAdmin(), is(true));
    }

    @Test
    public void recruitValidator_CorrectVerificationStatus() {
        Recruit testRecruit = new Recruit();
        testRecruit.setVerified(true);

        assertThat(testRecruit.getVerified(), is(true));
    }
}
