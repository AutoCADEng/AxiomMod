package eng.cad.auto.engine.client.render;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;

public class BeamEffect {
    ArrayList<Vec3> positions = new ArrayList<>();
    int lifespan;
    long creationTick;
    int tint;
    float width;
    ArrayList<Wave> waves = new ArrayList<>();
    int minSegments, maxSegments;
    BezierCurve shape = new BezierCurve();
    public BeamEffect(long creationTick){
        this.creationTick = creationTick;
    };

    public BeamEffect addPos(Vec3 pos){
        positions.add(pos);
        shape = shape.add(pos);
        return this;
    }

    public BeamEffect addPos(Vector3f pos){
        return addPos(new Vec3(pos));
    }

    public BeamEffect setTint(int color){
        this.tint = color;
        return this;
    }

    public BeamEffect width(float width){
        this.width = width;
        return this;
    }


    public BeamEffect setLifespan(int ticks) {
        this.lifespan = ticks;
        return this;
    }

    public BeamEffect maxSegments(int segments){
        this.maxSegments = segments;
        return this;
    }

    public BeamEffect minSegments(int segments) {
        this.minSegments = segments;
        return this;
    }

    public BeamEffect addWave(Wave wave){
        waves.add(wave);
        return this;
    }

    public float evaluateWave(float idx, float tick){
        float sum = 0.0f;
        float modified_tick = tick * 0.01f;
        for (Wave wave:waves){
            sum += wave.evaluate(idx - modified_tick);
        }
        return sum;
    }

    public static class Wave{
        float wavelength = 0.0f;
        float amplitude = 0.0f;
        float offset = 0.0f;
        public Wave(){}

        public Wave setWavelength(float val){
            wavelength = val;
            return this;
        }
        public Wave setAmplitude(float val){
            amplitude = val;
            return this;
        }

        public Wave setOffset(float val){
            offset = val;
            return this;
        }

        public float evaluate(float val){
            return amplitude * Mth.sin((2.0F * Mth.PI * (offset+val)) / wavelength) ;
        }
    }
}