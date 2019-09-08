# SNH-Introduction
This is a handwritten mathematical formula for offline recognition  and computing system, which has strong practicability and expansibility. At present, we sacrifice some system integrity to achieve higher accuracy. But you can still extend it as needed.  
This framework are twofold:  
1. **Java part:** mainly for formula logic analysis. reference--(https://github.com/woshiwpa/CoreMathImgProc)。   
2. **Python part** use CNN (convolutional neural network) to recognize single character. reference--(https://github.com/anujdutt9/Handwritten-Digit-Recognition-using-Deep-Learning/tree/master/CNN_Keras)  

More detail in the developer documentation. please refer to it by yourself.

## Requirement
### Java environment

- 在python下新建data文件夹
- 在res下新建prepresult文件夹

## [Summary of model features]---In general, try to write as clearly as possible
- 1.指数和底数大小要合适。
- 2.积分内部函数位置要适中(积分号写的大一点)。
- 3.极力避免黏连（过度切分机制还有待研究）。
- 4.累加累乘的上下界要参考积分的位置来写，并且下标要写成n=1而不是1。
- 5.矩阵的大括号或小括号要写大一点，包含内部所有元素。
- 6../.的点要圆一点。


