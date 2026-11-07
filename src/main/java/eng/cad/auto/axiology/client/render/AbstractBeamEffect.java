package eng.cad.auto.axiology.client.render;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.UUID;

public abstract class AbstractBeamEffect<T extends AbstractBeamEffect<T>> {
    public boolean dead = false;
    ArrayList<Vec3> positions = new ArrayList<>();
    int lifespan;
    long creationTick;
    int tint;
    boolean fadeable = false;
    UUID id;
    float width;
    float speed = 0.05f;
    ArrayList<Wave> waves = new ArrayList<>();
    int minSegments, maxSegments;
    BezierCurve shape = new BezierCurve();

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
    public T addPos (Vec3 pos){
        positions.add(pos);
        shape = shape.add(pos);
        return self();
    }

    public T addPos (Vector3f pos){
        return addPos(new Vec3(pos));
    }
    public T setUUID(UUID id){
        this.id = id;
        return self();
    }
    public T setTint (int color){
        this.tint = color;
        return self();
    }

    public T setWidth (float width){
        this.width = width;
        return self();
    }

    public T doFade(){
        this.fadeable = true;
        return self();
    }

    public T setLifespan (int ticks){
        this.lifespan = ticks;
        return self();
    }

    public T maxSegments (int segments){
        this.maxSegments = segments;
        return self();
    }

    public T minSegments (int segments){
        this.minSegments = segments;
        return self();
    }

    public T addWave (Wave wave){
        waves.add(wave);
        return self();
    }

    public float evaluateWave ( float idx, float tick){
        float sum = 0;
        for (Wave wave : waves) {
            sum += wave.evaluate(idx - tick * speed, width);
        }
        return sum/waves.size();
    }

    public T setTime (long gameTime){
        this.creationTick = gameTime;
        return self();
    }
    public T setSpeed (float var){
        this.speed = var;
        return self();
    }
    public abstract T createBeam(ResourceLocation texture, int tint, Vector3f castFrom, Vector3f castTo, Level level, UUID id);
}


