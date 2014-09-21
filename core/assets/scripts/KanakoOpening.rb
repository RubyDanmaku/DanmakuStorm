require 'java'
java_import com.github.bakabbq.bullets.Bullet
java_import com.github.bakabbq.bullets.BulletOval
java_import com.badlogic.gdx.math.MathUtils
class OpeningSlave1 < BossSlave
  def initialize(owner)
    super(owner)
    @oval = BulletOval.new(Bullet.COLOR_RED)
  end
  
  def updateShoot
    super
    every 25.frames do
      nway_shoot(@oval, 16, timer % 50, 12)
    end
  end
end

class OpeningSlave2 < BossSlave
  def initialize(owner)
    super(owner)
    @oval = BulletOval.new(Bullet.COLOR_GREEN)
  end
  
  def updateShoot
    super
    every 25.frames do
      nway_shoot(@oval, 16, timer % 50, 12)
    end
    
  end
end

class KanakoOpening < BaseScript
  def initialize
    super
    @oval = BulletOval.new(Bullet.COLOR_GREEN)
    @cursor = 0
  end
  
  def update
    super
    return if(@timer <= 60)
    @cursor += (@timer / 60) % 10
    every 25.frames do
      nway_shoot(@oval, 16, @cursor, 12)
    end
    
    if(@timer > 0)
      every 600.frames do
        move_to_pos rand(25) + 5, rand(30) + 35, 2
      end
      
    end
  end
  
  def call_slaves
    @slave1 = OpeningSlave1.new(self)
    @slave2 = OpeningSlave2.new(self)
    cur_x = cur_pos.x
    cur_y = cur_pos.y
    @slave1.direct_position_set(cur_x - 10, cur_y + 0)
    @slave2.direct_position_set(cur_y + 10, cur_y + 0)
    @slaves = [@slave1]
    @slaves.each do |s|
      register_slave(s)
      spawn_slave(s)
    end
  end
end
