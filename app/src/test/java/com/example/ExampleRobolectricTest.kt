package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.security.SecurityManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Öztürk Ailesi", appName)
  }

  @Test
  fun `security manager pin verification and family access works`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val securityManager = SecurityManager(context)
    val setupSuccess = securityManager.setupPin("1234", "En sevdiğin şehir?", "İstanbul")
    assertTrue(setupSuccess)
    assertTrue(securityManager.verifyPin("1234"))
    assertFalse(securityManager.verifyPin("9999"))

    // Test adding family member
    val addMemberSuccess = securityManager.addFamilyMember("Ayşe Öztürk", "Anne", "5678")
    assertTrue(addMemberSuccess)
    assertTrue(securityManager.verifyPin("5678"))

    // Test updating app title
    securityManager.setAppTitle("Öztürk Ailesi Özel")
    assertEquals("Öztürk Ailesi Özel", securityManager.appTitle.value)
  }
}
