require 'java'
java_import com.github.bakabbq.bullets.Bullet
java_import com.github.bakabbq.bullets.BulletAmulets
java_import com.github.bakabbq.bullets.BulletKnife
java_import com.github.bakabbq.bullets.BulletBigCircle
java_import com.github.bakabbq.bullets.BulletButterfly
java_import com.github.bakabbq.bullets.BulletGunshot
java_import com.github.bakabbq.bullets.BulletTriangle
java_import com.github.bakabbq.bullets.BulletKunai

TurningRedAmulet = Class.new BulletButterfly do
  def initialize
    super(Bullet.COLOR_RED)
  end
  
  def modifyBullet bullet
    super
    #srand(bullet.body.get_angle)
    if(bullet.timer <= 150)
      #bullet.body.set_transform(bullet.get_x, bullet.get_y, )
      bullet.set_speed(bullet.body.get_angle + 3,60)
    end
    
    
    
  end
end

class SnakeGod < BaseScript
  def initialize
    super
    @red = TurningRedAmulet.new
  end
  
  def update
    super
    if(@timer == 1)
      move_to_center
    end
    
    every 7 do
      srand
      nway_shoot(@red, 8,rand(360), rand(5) + 30)
    end
  end
  
end