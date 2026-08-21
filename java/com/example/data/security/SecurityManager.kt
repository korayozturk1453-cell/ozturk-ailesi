package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID

data class FamilyMember(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String, // e.g. "Baba", "Anne", "Çocuk", "Eş", "Aile Büyüğü"
    val pinHash: String,
    val isMaster: Boolean = false,
    val addedDate: Long = System.currentTimeMillis()
)

class SecurityManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ozturk_family_security_prefs", Context.MODE_PRIVATE)

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _isPinSet = MutableStateFlow(checkIfPinSet())
    val isPinSet: StateFlow<Boolean> = _isPinSet.asStateFlow()

    private val _appTitle = MutableStateFlow(prefs.getString(KEY_APP_TITLE, "Öztürk Ailesi") ?: "Öztürk Ailesi")
    val appTitle: StateFlow<String> = _appTitle.asStateFlow()

    private val _appSubtitle = MutableStateFlow(
        prefs.getString(KEY_APP_SUBTITLE, "Özel Aile Rehberi & Hatıra Defteri") ?: "Özel Aile Rehberi & Hatıra Defteri"
    )
    val appSubtitle: StateFlow<String> = _appSubtitle.asStateFlow()

    private val _categories = MutableStateFlow(loadCategories())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _allowedMembers = MutableStateFlow(loadFamilyMembers())
    val allowedMembers: StateFlow<List<FamilyMember>> = _allowedMembers.asStateFlow()

    private val _activeUser = MutableStateFlow<FamilyMember?>(null)
    val activeUser: StateFlow<FamilyMember?> = _activeUser.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean(KEY_IS_DARK_MODE, false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _dayWallpaperIndex = MutableStateFlow(prefs.getInt(KEY_DAY_WALLPAPER_INDEX, 0))
    val dayWallpaperIndex: StateFlow<Int> = _dayWallpaperIndex.asStateFlow()

    private val _nightWallpaperIndex = MutableStateFlow(prefs.getInt(KEY_NIGHT_WALLPAPER_INDEX, 0))
    val nightWallpaperIndex: StateFlow<Int> = _nightWallpaperIndex.asStateFlow()

    init {
        // If no PIN is configured, allow user to set up their PIN
        if (!checkIfPinSet()) {
            _isLocked.value = false
        }
    }

    private fun checkIfPinSet(): Boolean {
        return prefs.getString(KEY_PIN_HASH, null) != null
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.trim().toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // App Title & Subtitle Customization
    fun setAppTitle(newTitle: String) {
        val formatted = newTitle.trim().ifBlank { "Öztürk Ailesi" }
        prefs.edit().putString(KEY_APP_TITLE, formatted).apply()
        _appTitle.value = formatted
    }

    fun setAppSubtitle(newSubtitle: String) {
        val formatted = newSubtitle.trim().ifBlank { "Özel Aile Rehberi & Hatıra Defteri" }
        prefs.edit().putString(KEY_APP_SUBTITLE, formatted).apply()
        _appSubtitle.value = formatted
    }

    // Category Customization
    private fun loadCategories(): List<String> {
        val saved = prefs.getString(KEY_CATEGORIES, null)
        if (saved != null) {
            try {
                val jsonArr = JSONArray(saved)
                val list = mutableListOf<String>()
                for (i in 0 until jsonArr.length()) {
                    list.add(jsonArr.getString(i))
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                // fallback
            }
        }
        return DEFAULT_CATEGORIES
    }

    fun saveCategories(list: List<String>) {
        val jsonArr = JSONArray()
        list.forEach { jsonArr.put(it) }
        prefs.edit().putString(KEY_CATEGORIES, jsonArr.toString()).apply()
        _categories.value = list
    }

    fun addCategory(newCat: String): Boolean {
        val trimmed = newCat.trim()
        if (trimmed.isBlank() || _categories.value.any { it.equals(trimmed, ignoreCase = true) }) {
            return false
        }
        val updated = _categories.value + trimmed
        saveCategories(updated)
        return true
    }

    fun removeCategory(catToRemove: String): Boolean {
        if (catToRemove == "Tümü" || catToRemove == "Favoriler") return false
        val updated = _categories.value.filter { it != catToRemove }
        saveCategories(updated)
        return true
    }

    // Family Member Management
    private fun loadFamilyMembers(): List<FamilyMember> {
        val saved = prefs.getString(KEY_FAMILY_MEMBERS, null) ?: return emptyList()
        val list = mutableListOf<FamilyMember>()
        try {
            val jsonArr = JSONArray(saved)
            for (i in 0 until jsonArr.length()) {
                val obj = jsonArr.getJSONObject(i)
                list.add(
                    FamilyMember(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        name = obj.getString("name"),
                        role = obj.getString("role"),
                        pinHash = obj.getString("pinHash"),
                        isMaster = obj.optBoolean("isMaster", false),
                        addedDate = obj.optLong("addedDate", System.currentTimeMillis())
                    )
                )
            }
        } catch (e: Exception) {
            // ignore
        }
        return list
    }

    private fun persistFamilyMembers(list: List<FamilyMember>) {
        val jsonArr = JSONArray()
        list.forEach { m ->
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("name", m.name)
            obj.put("role", m.role)
            obj.put("pinHash", m.pinHash)
            obj.put("isMaster", m.isMaster)
            obj.put("addedDate", m.addedDate)
            jsonArr.put(obj)
        }
        prefs.edit().putString(KEY_FAMILY_MEMBERS, jsonArr.toString()).apply()
        _allowedMembers.value = list
    }

    fun addFamilyMember(name: String, role: String, pin: String): Boolean {
        if (name.isBlank() || pin.length < 4) return false
        val newMember = FamilyMember(
            name = name.trim(),
            role = role.trim().ifBlank { "Aile Üyesi" },
            pinHash = hashString(pin),
            isMaster = false
        )
        val updated = _allowedMembers.value + newMember
        persistFamilyMembers(updated)
        return true
    }

    fun removeFamilyMember(id: String): Boolean {
        val updated = _allowedMembers.value.filter { it.id != id }
        persistFamilyMembers(updated)
        return true
    }

    // PIN Setup & Verification
    fun setupPin(pin: String, securityQuestion: String, securityAnswer: String): Boolean {
        if (pin.length < 4) return false
        val hashedPin = hashString(pin)
        val hashedAnswer = hashString(securityAnswer.lowercase())

        // Save Master User
        val master = FamilyMember(
            name = "Aile Yöneticisi",
            role = "Yönetici",
            pinHash = hashedPin,
            isMaster = true
        )
        val currentMembers = _allowedMembers.value.filter { !it.isMaster }
        persistFamilyMembers(listOf(master) + currentMembers)

        prefs.edit()
            .putString(KEY_PIN_HASH, hashedPin)
            .putString(KEY_SECURITY_QUESTION, securityQuestion)
            .putString(KEY_SECURITY_ANSWER_HASH, hashedAnswer)
            .putBoolean(KEY_BIOMETRIC_ENABLED, true)
            .apply()

        _isPinSet.value = true
        _isLocked.value = false
        _activeUser.value = master
        return true
    }

    fun verifyPin(pin: String): Boolean {
        val enteredHash = hashString(pin)
        val storedMasterHash = prefs.getString(KEY_PIN_HASH, null)

        // Check if matches master
        if (storedMasterHash != null && enteredHash == storedMasterHash) {
            _isLocked.value = false
            _activeUser.value = _allowedMembers.value.firstOrNull { it.isMaster }
                ?: FamilyMember(name = "Aile Yöneticisi", role = "Yönetici", pinHash = storedMasterHash, isMaster = true)
            return true
        }

        // Check if matches any authorized family member
        val matchingMember = _allowedMembers.value.find { it.pinHash == enteredHash }
        if (matchingMember != null) {
            _isLocked.value = false
            _activeUser.value = matchingMember
            return true
        }

        return false
    }

    fun verifySecurityAnswer(answer: String): Boolean {
        val storedAnswerHash = prefs.getString(KEY_SECURITY_ANSWER_HASH, null) ?: return false
        val enteredAnswerHash = hashString(answer.lowercase())
        val isValid = enteredAnswerHash == storedAnswerHash
        if (isValid) {
            _isLocked.value = false
        }
        return isValid
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        if (!verifyPin(oldPin)) return false
        if (newPin.length < 4) return false
        val newHash = hashString(newPin)
        prefs.edit().putString(KEY_PIN_HASH, newHash).apply()

        // Update master user pin
        val updated = _allowedMembers.value.map {
            if (it.isMaster) it.copy(pinHash = newHash) else it
        }
        persistFamilyMembers(updated)
        return true
    }

    fun resetPinWithAnswer(newPin: String, answer: String): Boolean {
        if (!verifySecurityAnswer(answer)) return false
        if (newPin.length < 4) return false
        val newHash = hashString(newPin)
        prefs.edit().putString(KEY_PIN_HASH, newHash).apply()
        _isLocked.value = false
        return true
    }

    fun lockApp() {
        if (checkIfPinSet()) {
            _isLocked.value = true
            _activeUser.value = null
        }
    }

    fun unlockInstantly() {
        _isLocked.value = false
        if (_activeUser.value == null) {
            _activeUser.value = _allowedMembers.value.firstOrNull()
        }
    }

    fun getSecurityQuestion(): String {
        return prefs.getString(KEY_SECURITY_QUESTION, "Güvenlik Sorusunu Seçiniz")
            ?: "İlk evcil hayvanınızın adı nedir?"
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    // Day / Night Theme & Dynamic Background Management
    fun toggleDayNightMode(): Boolean {
        val newMode = !_isDarkMode.value
        prefs.edit().putBoolean(KEY_IS_DARK_MODE, newMode).apply()
        _isDarkMode.value = newMode
        // Every time we switch, we also rotate the corresponding theme wallpaper to give a fresh look!
        rotateWallpaper()
        return newMode
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK_MODE, enabled).apply()
        _isDarkMode.value = enabled
    }

    fun rotateWallpaper() {
        if (_isDarkMode.value) {
            val next = (_nightWallpaperIndex.value + 1) % 2
            prefs.edit().putInt(KEY_NIGHT_WALLPAPER_INDEX, next).apply()
            _nightWallpaperIndex.value = next
        } else {
            val next = (_dayWallpaperIndex.value + 1) % 3
            prefs.edit().putInt(KEY_DAY_WALLPAPER_INDEX, next).apply()
            _dayWallpaperIndex.value = next
        }
    }

    fun setDayWallpaper(index: Int) {
        val valid = index.coerceIn(0, 2)
        prefs.edit().putInt(KEY_DAY_WALLPAPER_INDEX, valid).apply()
        _dayWallpaperIndex.value = valid
    }

    fun setDayWallpaperIndex(index: Int) = setDayWallpaper(index)

    fun setNightWallpaper(index: Int) {
        val valid = index.coerceIn(0, 1)
        prefs.edit().putInt(KEY_NIGHT_WALLPAPER_INDEX, valid).apply()
        _nightWallpaperIndex.value = valid
    }

    fun setNightWallpaperIndex(index: Int) = setNightWallpaper(index)

    companion object {
        private const val KEY_PIN_HASH = "key_pin_hash"
        private const val KEY_SECURITY_QUESTION = "key_security_question"
        private const val KEY_SECURITY_ANSWER_HASH = "key_security_answer_hash"
        private const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
        private const val KEY_APP_TITLE = "key_app_title"
        private const val KEY_APP_SUBTITLE = "key_app_subtitle"
        private const val KEY_CATEGORIES = "key_categories"
        private const val KEY_FAMILY_MEMBERS = "key_family_members"
        private const val KEY_IS_DARK_MODE = "key_is_dark_mode"
        private const val KEY_DAY_WALLPAPER_INDEX = "key_day_wallpaper_index"
        private const val KEY_NIGHT_WALLPAPER_INDEX = "key_night_wallpaper_index"

        val DEFAULT_CATEGORIES = listOf(
            "Tümü",
            "Favoriler",
            "Seyahat",
            "Aile & Dostlar",
            "Özel Günler",
            "Doğa & Keşif",
            "Kişisel & Başarı"
        )
    }
}
