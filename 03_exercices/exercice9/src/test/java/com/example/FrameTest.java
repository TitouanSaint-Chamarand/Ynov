package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FrameTest {

    @Mock
    private IGenerateur generateur;

    @Test
    public void shouldIncreaseScoreWhenFirstRollIsMadeInStandardFrame() {
        when(generateur.randomPin(10)).thenReturn(3);
        Frame frame = new Frame(generateur, false);

        boolean accepted = frame.makeRoll();

        assertTrue(accepted);
        assertEquals(3, frame.getScore());
    }

    @Test
    public void shouldIncreaseScoreWhenSecondRollIsMadeInStandardFrame() {
        when(generateur.randomPin(10)).thenReturn(3);
        when(generateur.randomPin(7)).thenReturn(4);
        Frame frame = new Frame(generateur, false);
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertTrue(accepted);
        assertEquals(7, frame.getScore());
    }

    @Test
    public void shouldRejectSecondRollWhenStandardFrameStartsWithStrike() {
        when(generateur.randomPin(10)).thenReturn(10);
        Frame frame = new Frame(generateur, false);
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertFalse(accepted);
        assertEquals(10, frame.getScore());
    }

    @Test
    public void shouldRejectThirdRollWhenStandardFrameAlreadyHasTwoRolls() {
        when(generateur.randomPin(10)).thenReturn(3);
        when(generateur.randomPin(7)).thenReturn(4);
        Frame frame = new Frame(generateur, false);
        frame.makeRoll();
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertFalse(accepted);
        assertEquals(7, frame.getScore());
    }

    @Test
    public void shouldIncreaseScoreWhenSecondRollIsMadeAfterStrikeInLastFrame() {
        when(generateur.randomPin(10)).thenReturn(10, 5);
        Frame frame = new Frame(generateur, true);
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertTrue(accepted);
        assertEquals(15, frame.getScore());
    }

    @Test
    public void shouldAcceptThirdRollWhenLastFrameStartsWithStrike() {
        when(generateur.randomPin(10)).thenReturn(10, 5);
        when(generateur.randomPin(5)).thenReturn(3);
        Frame frame = new Frame(generateur, true);
        frame.makeRoll();
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertTrue(accepted);
    }

    @Test
    public void shouldAcceptSecondRollWhenLastFrameStartsWithStrike() {
        when(generateur.randomPin(10)).thenReturn(10, 5);
        Frame frame = new Frame(generateur, true);
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertTrue(accepted);
    }

    @Test
    public void shouldIncreaseScoreWhenThirdRollIsMadeAfterStrikeInLastFrame() {
        when(generateur.randomPin(10)).thenReturn(10, 5);
        when(generateur.randomPin(5)).thenReturn(2);
        Frame frame = new Frame(generateur, true);
        frame.makeRoll();
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertTrue(accepted);
        assertEquals(17, frame.getScore());
    }

    @Test
    public void shouldAcceptThirdRollWhenLastFrameStartsWithSpare() {
        when(generateur.randomPin(10)).thenReturn(7, 4);
        when(generateur.randomPin(3)).thenReturn(3);
        Frame frame = new Frame(generateur, true);
        frame.makeRoll();
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertTrue(accepted);
    }

    @Test
    public void shouldIncreaseScoreWhenThirdRollIsMadeAfterSpareInLastFrame() {
        when(generateur.randomPin(10)).thenReturn(7, 4);
        when(generateur.randomPin(3)).thenReturn(3);
        Frame frame = new Frame(generateur, true);
        frame.makeRoll();
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertTrue(accepted);
        assertEquals(14, frame.getScore());
    }

    @Test
    public void shouldRejectThirdRollWhenLastFrameHasNoStrikeOrSpare() {
        when(generateur.randomPin(10)).thenReturn(3);
        when(generateur.randomPin(7)).thenReturn(4);
        Frame frame = new Frame(generateur, true);
        frame.makeRoll();
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertFalse(accepted);
        assertEquals(7, frame.getScore());
    }

    @Test
    public void shouldAcceptThirdRollWhenLastFrameHasTwoStrikes() {
        when(generateur.randomPin(10)).thenReturn(10, 10, 7);
        Frame frame = new Frame(generateur, true);
        frame.makeRoll();
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertTrue(accepted);
        assertEquals(27, frame.getScore());
    }

    @Test
    public void shouldRejectFourthRollInLastFrame() {
        when(generateur.randomPin(10)).thenReturn(10, 5);
        when(generateur.randomPin(5)).thenReturn(2);
        Frame frame = new Frame(generateur, true);
        frame.makeRoll();
        frame.makeRoll();
        frame.makeRoll();

        boolean accepted = frame.makeRoll();

        assertFalse(accepted);
        assertEquals(17, frame.getScore());
    }
}
