require 'java'
java_import com.github.bakabbq.bullets.Bullet
java_import com.github.bakabbq.bullets.BulletAmulets
java_import com.github.bakabbq.bullets.BulletOval
java_import com.github.bakabbq.bullets.BulletTriangle
java_import com.badlogic.gdx.math.MathUtils
RedRotatingAmulet = Class.new  BulletTriangle do
  def initialize
    super(Bullet.COLOR_RED)
  end
  
  def modify_bullet bullet
    super
    bullet.set_speed(10 + bullet.timer / 60)
  end
end


BlueRotatingAmulet = Class.new  BulletOval do
  def initialize
    super(Bullet.COLOR_BLUE)
  end
  
  
  def modifyBullet bullet
    super
    bullet.set_speed(8 + bullet.timer / 20)
  end
end

class DivineExplosion < BaseScript
  def initialize
    super
    @red_amulet = RedRotatingAmulet.new
    @blue_amulet = BlueRotatingAmulet.new
    
    @cur_1 = 270
    @cur_2 = 90
  end
  
  
  def update
    super
    if(@moved.nil?)
      move_to_center
      @moved = true
    end
    @cur_1 += 1
    @cur_2 -= 1
    every 40.frames do
      #nWayAngeledSpreadShot(@blue_amulet, 5, 4, player_angle + 10, 360 / 5, 8)
      nway_shoot(@blue_amulet, 30, player_angle + 3, 8)
    end
    
    every 8.frames do
      shoot(@red_amulet, @cur_1, 10)
      shoot(@red_amulet, @cur_2, 10)
    end
    
  end
  
  
end
