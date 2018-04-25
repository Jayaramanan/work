import os;
import shutil;

#path to resource folder
path = r"./src/main/resources"

#folder to take configs from
source_folder = "default"

#list of files to be copied
files_to_copy = ["web.xml", "spring-beans.xml", "database.version"]

#folders that should not receive files
excluded_targets = ["default", ".svn"]

print "target folders:	"
print os.listdir(path)
print ""
print "files to copy:	" 
print files_to_copy
print ""
for f in os.listdir(path):
	if (f not in excluded_targets and os.path.isdir(os.path.join(path, f))):
		print "copying to " + f;
		for source in files_to_copy:
			source_path = os.path.join(path, source_folder, source)
			target_path = os.path.join(path, f)
			shutil.copy(source_path, target_path)
print ""
print "done OK"