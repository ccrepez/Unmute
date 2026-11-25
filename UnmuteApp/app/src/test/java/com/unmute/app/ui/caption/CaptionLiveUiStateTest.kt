package com.unmute.app.ui.caption

import com.unmute.app.ui.caption.CaptionLiveUiState.Companion.DEFAULT_FONT_SP
import com.unmute.app.ui.caption.CaptionLiveUiState.Companion.FONT_STEP_SP
import com.unmute.app.ui.caption.CaptionLiveUiState.Companion.MAX_FONT_SP
import com.unmute.app.ui.caption.CaptionLiveUiState.Companion.MIN_FONT_SP
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CaptionLiveUiStateTest {

    @Test
    fun largerFontIncreasesByOneStep() {
        assertEquals(DEFAULT_FONT_SP + FONT_STEP_SP, CaptionLiveUiState().withLargerFont().fontSizeSp)
    }

    @Test
    fun smallerFontDecreasesByOneStep() {
        assertEquals(DEFAULT_FONT_SP - FONT_STEP_SP, CaptionLiveUiState().withSmallerFont().fontSizeSp)
    }

    @Test
    fun fontNeverExceedsMaximum() {
        var state = CaptionLiveUiState()
        repeat(50) { state = state.withLargerFont() }
        assertEquals(MAX_FONT_SP, state.fontSizeSp)
        assertFalse(state.canIncreaseFont)
        assertTrue(state.canDecreaseFont)
    }

    @Test
    fun fontNeverGoesBelowMinimum() {
        var state = CaptionLiveUiState()
        repeat(50) { state = state.withSmallerFont() }
        assertEquals(MIN_FONT_SP, state.fontSizeSp)
        assertFalse(state.canDecreaseFont)
        assertTrue(state.canIncreaseFont)
    }
}
