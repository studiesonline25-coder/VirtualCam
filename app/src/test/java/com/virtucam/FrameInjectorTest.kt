package com.virtucam

import android.graphics.Bitmap
import android.graphics.Color
import com.virtucam.core.FrameInjector
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for FrameInjector
 * Tests bitmap to YUV conversion
 */
class FrameInjectorTest {
    
    private lateinit var frameInjector: FrameInjector
    
    @Before
    fun setup() {
        frameInjector = FrameInjector()
    }
    
    @Test
    fun `createYuvBuffer returns non-null buffer`() {
        // Create a simple test bitmap
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.RED)
        
        val buffer = frameInjector.createYuvBuffer(bitmap, 100, 100)
        
        assertNotNull(buffer)
        assertTrue(buffer.capacity() > 0)
        
        bitmap.recycle()
    }
    
    @Test
    fun `createYuvBuffer scales bitmap correctly`() {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.BLUE)
        
        val targetWidth = 100
        val targetHeight = 100
        
        val buffer = frameInjector.createYuvBuffer(bitmap, targetWidth, targetHeight)
        
        // YUV420 size = width * height * 1.5
        val expectedSize = (targetWidth * targetHeight * 1.5).toInt()
        assertEquals(expectedSize, buffer.capacity())
        
        bitmap.recycle()
    }
    
    @Test
    fun `YUV conversion produces valid Y values`() {
        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        
        val buffer = frameInjector.createYuvBuffer(bitmap, 10, 10)
        
        // Y value for white (RGB=255,255,255) should be around 235
        val yValue = buffer.get(0).toInt() and 0xFF
        assertTrue("Y value should be high for white pixel", yValue > 200)
        
        bitmap.recycle()
    }
}
