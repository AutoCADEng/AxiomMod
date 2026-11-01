package eng.cad.auto.engine.client.render;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;

public abstract class AbstractBeamEffect {
    ArrayList<Vec3> positions = new ArrayList<>();
    int lifespan;
    long creationTick;
    int tint;
    float width;
    float speed = 0.05f;
    ArrayList<Wave> waves = new ArrayList<>();
    int minSegments, maxSegments;
    BezierCurve shape = new BezierCurve();

    public AbstractBeamEffect addPos (Vec3 pos){
        positions.add(pos);
        shape = shape.add(pos);
        return this;
    }

    public AbstractBeamEffect addPos (Vector3f pos){
        return addPos(new Vec3(pos));
    }

    public AbstractBeamEffect setTint (int color){
        this.tint = color;
        return this;
    }

    public AbstractBeamEffect width (float width){
        this.width = width;
        return this;
    }


    public AbstractBeamEffect setLifespan (int ticks){
        this.lifespan = ticks;
        return this;
    }

    public AbstractBeamEffect maxSegments (int segments){
        this.maxSegments = segments;
        return this;
    }

    public AbstractBeamEffect minSegments (int segments){
        this.minSegments = segments;
        return this;
    }

    public AbstractBeamEffect addWave (Wave wave){
        waves.add(wave);
        return this;
    }

    public float evaluateWave ( float idx, float tick){
        float sum = 0;
        for (Wave wave : waves) {
            sum += wave.evaluate(idx - tick * speed);
        }
        return width + sum;
    }

    public AbstractBeamEffect setTime (long gameTime){
        this.creationTick = gameTime;
        return this;
    }
    public AbstractBeamEffect setSpeed (float var){
        this.speed = var;
        return this;
    }
    public abstract AbstractBeamEffect getNew(Vector3f castFrom, Vector3f castTo, Level level);
}


