package net.maelbrancke.filip;

import android.util.FloatMath;

/**
 * Dynamics class.
 */
public class Dynamics {

    /**
     * Tolerance to compare floats.
     */
    private static final float TOLERANCE = 0.01f;

    /**
     * Target position.
     */
    private float targetPosition;

    /**
     * The current position.
     */
    private float position;

    /**
     * The current velocity.
     */
    private float velocity;

    /**
     * The amount of springiness.
     */
    private float springiness;

    /**
     * The amount of damping. Needs to be a number between 0 and 1.
     */
    private float damping;

    /**
     * The time of the last update.
     */
    private long lastTime;

    public Dynamics(float springiness, float dampingRatio) {
        this.springiness = springiness;
        this.damping = dampingRatio * 2 * FloatMath.sqrt(springiness);
    }

    public void update(final long now) {
        final float timeDifference = Math.min(now - lastTime, 50) / 1000f;
        final float distance = position - targetPosition;
        final float acceleration = -springiness * distance - damping * velocity;

        velocity += acceleration * timeDifference;
        position += velocity * timeDifference;

        lastTime = now;
    }

    public boolean isAtRest() {
        final boolean isStandingStill = Math.abs(velocity) < TOLERANCE;
        final boolean isAtTarget = (targetPosition - position) < TOLERANCE;
        return isStandingStill && isAtTarget;
    }

    public float getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(final float targetPosition, final long now) {
        this.targetPosition = targetPosition;
        lastTime = now;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(final float velocity, final long now) {
        this.velocity = velocity;
        lastTime = now;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(final float position, final long now) {
        this.position = position;
        lastTime = now;
    }
}
