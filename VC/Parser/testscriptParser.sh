#! /bin/zsh
export CLASSPATH=/Users/lalithaseshadri/COMP3131Assignments/out/production/COMP3131:/Users/lalithaseshadri/Uni-Scanner
for i in `ls *.vc`
do
	echo $i:
	java VC.vc $i
	java VC.vc -u ${i}uu ${i}u
        diff ${i}u ${i}uu
done