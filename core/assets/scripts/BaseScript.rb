require 'java'
java_import com.badlogic.gdx.math.MathUtils;
$: << "."
puts
module Kernel
  def library(str)
    #load("./scripts/#{str}.rb")
    #load("#{Dir.pwd}/scripts/#{str}.rb")
    raise
  end
  
  
  def cas_to_angle(x, y)
    return MathUtils.atan2(y,x)
  end
  
end


java_import com.github.bakabbq.shooters.bosses.ThBoss
class ThBoss
  attr_accessor :slaves
  def slaves
    @slaves ||= []
  end
  
  def register_slave(slave)
    @slaves ||= []
    @slaves << slave
  end
end

java_import com.github.bakabbq.shooters.EnemyShooter
class EnemyShooter
  def playerAngle
    
  end
end




#puts $LOAD_PATH


class Object
	def this
		return self
	end
end
java_import com.github.bakabbq.bullets.BulletDef


class BulletDef
	def recolor(color_id)
		self.colorId = color_id
		setTextureIndex
		updateTexture
		initCreationTexture
	end
end
class Fixnum
	def frames()
		return self
	end
	
	alias frame frames
	
	def minutes
		return self * 60
	end
	
	alias minute minutes
end
require 'java'
java_import com.github.bakabbq.shooters.bosses.ThBoss
java_import com.github.bakabbq.shooters.EnemyShooter
module Aliases
  module Script

  end

  module Enemy
    def body
      return enemyBody
    end

    def cur_pos
      return enemyBody.get_position
    end

    def set_linear_speed x, y
      body.setLinearVelocity(x,y)
    end
    alias set_linear_velocity set_linear_speed
  end

  module Boss
  end


end

class BaseScript
  include Aliases::Script
end

class ThBoss
  include Aliases::Boss
end

class EnemyShooter
  include Aliases::Enemy
end

class BaseScript
	include_package com.github.bakabbq.bullets
	attr_accessor	:owner
	attr_accessor	:timer

	attr_accessor	:schedule_movement_timer, :schedule_x, :schedule_y
	def initialize
		@timer = 0
		@schedule_movement_timer = 0
	end
	
	def every(cycle, interval = 0)
		if(@timer % cycle == interval)
			yield if block_given?
		end
	end
  
  def player_angle
    MathUtils.atan2(getY() - getPlayer().getY(), getX() - getPlayer().getX()) * 180 / Math::PI
  end

	def update
		@timer += 1
    if(@switch.nil?)
      move_to_desired_position
      @switch = 1
    end
		update_schedule
	end
  
  
  def position_set_init
    direct_position_set(10,140)
  end
  
  def body
    return owner.enemyBody
  end
  

	def velocity_movement(x, y, damping)
		owner.enemyBody.setLinearDamping(damping)
		owner.enemyBody.set_linear_velocity(x,y)
	end
  
  
  def move_to_desired_position
    
    if(owner.slaves)
      to_delete = []
      owner.slaves.each do |s|
        s.receiveDamage(1000000)
        owner.slaves.delete s
        puts "wheee"
      end
    end
  
    puts owner.slaves.length
    #position_set_init
    move_to_uppercenter
  end
  

  
  

	def direct_position_set(x,y)
		owner.enemyBody.set_transform(x,y,0)
	end

	def schedule_movement(vX, vY, time)
		@schedule_x = vX
		@schedule_y = vY
		@schedule_movement_timer = time
	end

	def update_schedule
		@schedule_movement_timer -= 1
		if(@schedule_movement_timer > 20)
			owner.enemyBody.set_linear_velocity(@schedule_x, @schedule_y)
		elsif @schedule_movement_timer > 0
			owner.enemyBody.setLinearDamping(3)
			owner.enemyBody.set_linear_velocity(@schedule_x, @schedule_y) # yeah being lazy
		end
	end

	
	def method_missing(name, *params, &block)
		if self.owner.respond_to? name
			self.owner.send(name, *params, &block)
		else
			super(name, *params, &block)
		end
	end
end

java_import com.github.bakabbq.shooters.EnemyShooter

class BossSlave < EnemyShooter
  def initialize(owner)
    super(owner.ground)
    @owner = owner
    @timer = 0;
  end
  
  def every interval, subinterval = 0
    if(timer % interval == subinterval)
      yield
    end
    
  end
  
  
  
  def updateShoot
    super
    @timer+=1
  end
  
  
  def isSlave
    return true
  end
end


module BulletDB
	
end
java_import com.github.bakabbq.shooters.bosses.ThBoss
java_import com.github.bakabbq.shooters.EnemyShooter
module BossMovement
  def move_to_pos target_x, target_y, time
    @move_to_pos_timer = 1000000
    @target_x = target_x
    @target_y = target_y
    dif_x = target_x - cur_pos.x
    dif_y = target_y - cur_pos.y
    #puts "Frome: #{cur_pos.x}, #{cur_pos.y} Final Difference#{dif_x}, #{dif_y}"
    #puts "#{dif_x / time.to_f}, #{dif_y / time.to_f}"
    set_linear_speed dif_x / time.to_f, dif_y / time.to_f
  end

  def update_move_to_pos
    #puts "b"
    return unless @move_to_pos_timer
    #puts "c"
    @move_to_pos_timer -= 1
    #puts @move_to_pos_timer
    return unless @target_x && @target_y
    dif_x = @target_x - cur_pos.x
    dif_y = @target_y - cur_pos.y
    #puts "#{dif_x}, #{dif_y}"
    if (dif_x - 0).abs <= 0.05 && (dif_y - 0).abs <= 0.05#@move_to_pos_timer <= 0
      @move_to_pos_timer = 0

      set_linear_speed 0, 0
    end
  end

  # hook method
  def onActive

  end


  def onLeave
    @leaving = true
    move_to_pos 20, 40, 1.5
  end

  def move_to_uppercenter
    move_to_pos 23, 62, 2
  end
  
  def move_to_center
    move_to_pos 23, 40, 2
  end
  


  def finishedLeaving
    return @leaving && (@move_to_pos_timer == 0)
  end

  def moving?
    return true unless @move_to_pos_timer
    return @move_to_pos_timer > 0
  end

end

class EnemyShooter
	def direct_position_set(x,y)
		enemyBody.set_transform(x,y,0)
	end
end


class ThBoss
  include BossMovement
  alias boss_movement_updateShoot updateShoot
  def updateShoot
    boss_movement_updateShoot
    update_move_to_pos
  end
end

class BaseScript
  alias boss_movement_script_update update
  def update
    boss_movement_script_update
    owner.update_move_to_pos
  end
end





