package com.cartoonishvillain.villainousfishai;


/*
This class was designed only because the AvoidEntity goal class only allows Living Entities to exist.
This implementation allows any entity to be avoidable.
 */

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Predicate;

public class FishAvoidGoal<T extends Entity> extends Goal {
    protected final Mob avoider;
    protected final double speedmodifier;
    protected T entityToAvoid;
    protected final float biggestreaction;
    protected Class<T> entityClassToAvoid;
    protected final Predicate<Entity> extraAvoidDetails;
    protected Path path;
    protected final PathNavigation pathNav;


    public FishAvoidGoal(Mob avoider, Class<T> entityToAvoid, float biggestreaction, double speedmodifier, Predicate<Entity> extradetails){
        this.avoider = avoider;
        this.entityClassToAvoid = entityToAvoid;
        this.biggestreaction = biggestreaction;
        this.speedmodifier = speedmodifier;
        this.pathNav = avoider.getNavigation();
        extraAvoidDetails = extradetails;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        float prevdistance = Float.MAX_VALUE;
        //find the nearest avoidable entity.
        for(T entity :  avoider.level.getEntitiesOfClass(this.entityClassToAvoid, avoider.getBoundingBox().inflate(this.biggestreaction))){
            float distance = avoider.distanceTo(entity);
            if(distance < prevdistance && extraAvoidDetails.test(entity)){
                prevdistance = distance;
                entityToAvoid = entity;
            }
        }

        if(entityToAvoid == null) return false;
        else {
            Vec3 vector3d = DefaultRandomPos.getPosAway((PathfinderMob) this.avoider, 16, 7, this.entityToAvoid.position());
            if (vector3d == null) return false;
            else if (this.entityToAvoid.distanceToSqr(vector3d.x, vector3d.y, vector3d.z) < this.entityToAvoid.distanceToSqr(this.avoider))
                return false;
            else {
                this.path = this.pathNav.createPath(vector3d.x, vector3d.y, vector3d.z, 0);
                return this.path != null;
            }
        }
    }

    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }

    public void start() {
        this.pathNav.moveTo(this.path, this.speedmodifier);
    }

    public void stop() {
        this.entityToAvoid = null;
    }

    public void tick() {
        this.avoider.getNavigation().setSpeedModifier(this.speedmodifier);

    }
}
