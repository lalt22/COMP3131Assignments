#! /bin/zsh
export CLASSPATH=/Users/lalithaseshadri/COMP3131Assignments/out/production/COMP3131:/Users/lalithaseshadri/Uni-Scanner
for i in `ls t*.vc`
do
	echo $i:
	java VC.vc $i
        diff ${i}u `basename $i .vc`.sol
done
