#! /bin/zsh
rm *.xxx
export PROJECT_DIR="/Users/lalithaseshadri/COMP3131Assignments"
for i in `ls $PROJECT_DIR/VC/Recogniser/t*.vc`
do
	echo $i:
	b=`basename $i .vc`
	echo $b
	java -cp $PROJECT_DIR/out/production/COMP3131:/Users/lalithaseshadri/Uni-Scanner VC.vc $i > $b.xxx
	diff $b.xxx $PROJECT_DIR/VC/Recogniser/$b.sol
done
