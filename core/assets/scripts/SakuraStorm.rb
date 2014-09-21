require 'java'
java_import com.github.bakabbq.bullets.Bullet
java_import com.github.bakabbq.bullets.BulletOval
java_import com.badlogic.gdx.math.MathUtils

class InitialBullet < BulletOval
  def initialize
    super(Bullet.COLOR_GREEN)
    @possible_outcomes = [BulletOval.new(Bullet.COLOR_PURPLE), SpeedyBullet.new, SpeedyBullet2.new]
  end
  
  def modifyBullet b
    super
    #puts b.timer
    if(b.timer == 40)
      b.set_angle(b.get_angle + 270)
    end
    
    #b.accelerate 1.0/60.0
  end
end

class InitialBullet2 < BulletOval
  def initialize
    super(Bullet.COLOR_GREEN)
    @possible_outcomes = [BulletOval.new(Bullet.COLOR_PURPLE), SpeedyBullet.new, SpeedyBullet2.new]
  end
  
  def modifyBullet b
    super
    #puts b.timer
    if(b.timer == 40)
      b.set_angle(b.get_angle - 270)
    end
    
    #b.accelerate 1.0/60.0
  end
end

class SpeedyBullet < BulletOval
  def initialize
    super(Bullet.COLOR_VIOLET)
  end
  
  def modifyBullet b
    b.accelerate 1
  end
end

class SpeedyBullet2 < BulletOval
  def initialize
    super(Bullet.COLOR_BLUE)
  end
  
  def modifyBullet b
    b.accelerate 2.0/60.0
  end
end


class SakuraStorm < BaseScript
  def initialize
    super
    @oval = InitialBullet.new
    @oval2 = InitialBullet2.new
    @cursor = 0
    @cnt = 0
  end
  
  def update
    super
    @cursor += (@timer / 60) % 10
    every 60.frames do
      @cnt+=1
      nway_shoot(@oval, 20, @cursor, 12)
      nway_shoot(@oval2, 20, 360/20/2 + @cursor, 12)
    end
  end
  
end
