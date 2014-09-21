require 'java'
java_import com.github.bakabbq.bullets.Bullet
java_import com.github.bakabbq.bullets.BulletAmulets
java_import com.github.bakabbq.bullets.BulletKnife
java_import com.github.bakabbq.bullets.BulletBigCircle
java_import com.github.bakabbq.bullets.BulletButterfly
java_import com.github.bakabbq.bullets.BulletGunshot
java_import com.github.bakabbq.bullets.BulletTriangle
java_import com.github.bakabbq.bullets.BulletKunai

class TestSlave < BossSlave
  def initialize(owner, angle, mirror)
    super(owner)
    @red_amulet = BulletAmulets.new(Bullet.COLOR_BLUE)
    @blue_amulet = BulletAmulets.new(Bullet.COLOR_PURPLE)
    @angle = angle
    @mirror = mirror
  end
  
  def isSlave
    return true
  end
  
  def updateShoot
    angle_affix = timer % 360
    angle_affix = 360 - timer % 360 if @mirror
    if(timer % 20 ==0 )
      shoot(@red_amulet, angle_affix + @angle, 8)
    end
    
    if(timer % 25 == 0)
      shoot(@blue_amulet, angle_affix + 45 + @angle, 8)
    end
  end
  
end

class TestScript < BaseScript
	def initialize
		super
		@test_bullet = BulletTriangle.new(0);
		@amulet = BulletAmulets.new(0);
		@bigCircle = BulletBigCircle.new(0)
		@kunai = BulletKunai.new(3);
	end
  
  def update
  	super
    #puts moving?
    
    return if moving?
    
    if(@slave_called.nil?)
      call_slaves
      @slave_called = 1
    end
    
    every 60.frames do
      nway_shoot(@amulet,8,timer % 360,8)
      #move_to_pos 10, 30, 2
    end
      
  end
  
  def call_slaves
    
    
    slave_ld = TestSlave.new(self,315, true)
    slave_rd = TestSlave.new(self,45, false)
    slave_lu = TestSlave.new(self,215, true)
    slave_ru = TestSlave.new(self,135, false)
    cur_x = cur_pos.x
    cur_y = cur_pos.y
    
    dis = 10
    slave_ld.direct_position_set(cur_x - dis, cur_y - dis)
    slave_rd.direct_position_set(cur_x + dis, cur_y - dis)
    slave_lu.direct_position_set(cur_x - dis, cur_y + dis)
    slave_ru.direct_position_set(cur_x + dis, cur_y + dis)
    @slaves = [slave_ld, slave_rd, slave_lu, slave_ru]
    @slaves.each do |s|
      register_slave(s)
      spawn_slave(s)
    end
    
  end
  
    
    def on_active
      # mostly_used
      #move_to_pos 10, 30, 2
    end
    
    
    
    
    
    
    
    
    
    
=begin
        every 140.frames do
          slave = TestSlave.new(self)
          slave.setX(self.getX)
          slave.setY(self.getY)
          spawnSlave(slave)
        end
=end
    
end