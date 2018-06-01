include_class 'applet.ACMain'

class Fixture
	def start_application
		args = []
		ACMain.main(args.to_java:String)
	end

	def teardown
		
	end

	def setup
		start_application
	end

	def test_setup
		
	end

end

$fixture = Fixture.new
