require 'java'
java_import com.github.bakabbq.bullets.Bullet
class ShotDef
	attr_accessor	:datas # array in array, [BulletDef, Angle, Speed, Tag(Symbol)]
	def initialize
		@datas = []

		@defaults = {
			:speed => 0,
			:bd    => Bullet.amuletBullet,
		}
	end

	def add_entry(angle, speed = 0, bd = 0, tag = :default)
		speed = @defaults[:speed] if speed == 0
		bd = @defaults[:bd] if bd == 0
		@datas << [angle, speed, bd, tag]
		return self
	end

	class ShotPart
		def initialize(angle, speed = 0, bd = 0, tag = :default)
			@angle = angle
		end

		def alter(data)
			#just overide it
		end
	end

	class OneWay < ShotPart
	end

end