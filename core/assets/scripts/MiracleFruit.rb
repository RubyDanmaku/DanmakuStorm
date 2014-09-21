require 'java'
java_import com.github.bakabbq.bullets.Bullet
java_import com.github.bakabbq.bullets.BulletOval
java_import com.github.bakabbq.bullets.BulletTriangle
class MiracleFruitSlave < BossSlave
  def initialize(owner)
    super(owner)
    @shooting_timer = 1000
    @red_oval = BulletOval.new(Bullet.COLOR_RED)
    hp = 300
  end
  
  def start_shooting
    @shooting_timer = 0
  end
  
  def updateShoot
    super
    shooting_timer = @shooting_timer
    if shooting_timer <= 60
      @shooting_timer+=1
      if(shooting_timer % 20 == 0)
        nway_shoot(@red_oval, 25, 0, 18)
      end
    end
    if shooting_timer > 60
      receive_damage 3000
    end
    
  end
  
end


class MiracleFruit < BaseScript
  def initialize
    super
    @red_oval = BulletOval.new(Bullet.COLOR_RED)
  end
  
  def update
    super
    if(@timer == 1)
      call_slaves
    end
    every 360, 1 do
      call_slaves
    end
    
    every 360, 120 do
      move_to_pos rand(25) + 5, rand(30) + 35, 2
    end
    
  end
  
  def call_slaves
    d = 20
    a =  10.0 * 2 ** 0.5
    @unregistered_slaves = Array.new(8){MiracleFruitSlave.new(self)}
    [
      [a, a],
      [20,0],
      [a, -a],
      [-a,a],
      [-a,-a],
      [-20,0],
      [0,20],
      [0,-20]
    ].each_with_index do |c, i|
      s = @unregistered_slaves[i]
      s.direct_position_set(cur_pos.x + c[0], cur_pos.y + c[1])
      register_slave s
      spawn_slave s
      s.start_shooting
    end
    
  end
  
  
end
