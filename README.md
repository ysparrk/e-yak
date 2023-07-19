- 07.17 figma!

![figma](./figma(rev.1).png)

![figma](./figma(rev.2).PNG)

금일 피그마를 팀원들과 공유하며 나온 이슈들에 대하여 정리합니다.  
1. 회원가입시 초기 정보  
회원가입시 필요한 정보는 닉네임, 5개의 시간(기상, 아침, 점심, 저녁 식사 시간, 취침 시간)과 식사 시간으로 생각됩니다.  
필요없는 정보는 제거해도 될 것 같습니다.  
2. 메인 페이지  
메인 페이지에는 두가지 의견이 있었습니다.  
첫 번째로는 시간에 흐름에 따라서 시간 단위로 알람을 배치하는 것이고 두 번째로는 약에 따라 과거, 현재와 미래로 분류하는 것입니다.  
사용자가 사용하기에 시간에 흐름에 따라 메인 화면을 구축하는 것이 좋을 것 같다고 생각하였기 때문에 시간의 흐름에 따라 알람을 배치하는 방법으로 구성하였습니다.  
또한, 알람과 관련되어 지난 피그마에 존재하였던 알람 미루기 기능을 제거하고 복용 버튼만을 활성화하였습니다.  
위와 같이 구성한 이유는 복용하지 않았을 경우 알람을 15분 단위로 3번 정도 주는 것이 적당하다고 생각하였기 때문입니다.  
3. 본인의 복약 조회 및 등록  
본인의 복약 조회에서 달력과 같은 방식을 사용하여 보다 한눈에 보기 쉽도록 구성하였습니다.  
PDF 출력을 위한 조회 기능을 추가하였습니다.  
OCR을 사용한 방법으로 데이터를 불러올 수 있으면 사용자의 편의성이 한층 더 높아질 것으로 생각합니다.  
식전, 식후의 복약 지도에 맞도록 식사 시간 알림에 대하여 2가지로 구성할 수 있도록 구성하였습니다.  
약의 이모티콘을 설정하는 방법은 보다 단순한 방법으로 변형될 것 같습니다.  
4. 가족 설정 페이지  
사용자들은 서로의 투약 정보를 열람할 수 있습니다.  
우리는 이러한 사용자들을 "가족"이라는 단어를 사용합니다.  
가족은 서로가 서로를 "팔로우" 할 수 있으며 공개 범위를 정할 수 있습니다.  
오늘 논의된 공개 범위는 두 가지로써 달력 공개와 전체 공개로 구성됩니다.  
사용자가 공개 범위를 직접 선택함으로써 자신의 데이터를 보호할 수 있을 것 입니다.  

마지막으로 약통 설정 페이지에서는 이전에 논의했던 내용이 그대로 반영되었습니다.

# 코틀린  

## 1. 기본 구문  
### 1.1  변수와 상수  
```kotlin
var a: Int = 10 // 변수, var [변수명]: [자료형] = [값] -> var a = 10
val b: Int = 20 // 상수, val [상수명]: [자료형] = [값] -> val b = 20
```  
### 1.2 함수
```kotlin
fun greet(str: String): Unit {  // 함수, fun [함수명]([인수명]: [자료형]): [반환자료형] {

}
```

# 2. 기본 자료형  
## 2.1 숫자형  
|자료형|뜻|리터럴|
|------|--------------|---|
|Double|64비트 부동소수점||
|Float|32비트 부동소수점|f|
|Long|64비트 정수|L|
|Int|32비트 정수||
|Short|16비트 정수||
|Byte|8비트 정수||

## 2.2 문자형  
|자료형|뜻|리터럴|
|------|--------------|---|
|String|문자열|"~~", """ ~~~~~ """|
|Char|하나의 문자|'~~'|

## 2.3 배열  
```kotlin
val numbers: Array<Int> = arrayOf(1, 2, 3)  // val [배열명]: Array<[자료형]> = arrayOf([값1], [값2], [값3]) -> val numbers = arrayOf(1, 2, 3)
```
# 3. 제어문  
## 3.1 if  
## 3.2 when  
자동으로 break가 있음  
```kotlin
val a: Int = 1

when (x) {
    1 -> println("x == 1")  // [값] -> [동작]
    2, 3 -> println("x == 2 or x == 3") // [값1], [값2] -> [동작], [값1] or [값2]
    in 4..7 -> println("4부터 7사이")   // in [값1]..[값2] -> [동작], [값1] <= and <= [값2]
    !in 8..9 -> println("8부터 10 사이가 아님") // !in [값1]..[값2] -> [동작], >= [값1] or [값2] <=
    else -> {   // 나머지
        print(x 는 1이나 2가 아님")
    }
}
```

## 3.3 for  
```kotlin
val numbers: Array<Int> = arrayOf(1, 2, 3, 4)

for(num in numbers){    // for([요소] in [배열]) {
    
}

for(i in 1..3){ // for([변수] [시작값]..[끝값]) {

}

for(i in 0..10 step 2){ // for([변수] in [시작값]..[끝값] step [증가 간격])

}

for(i in 10 downTo 0 step 2){   // for([변수] in [시작값] downTo [끝값] step [감소 간격])

}
```

## 3.4 while  

# 4. 클래스  

# 5. 인터페이스

# 6. null 가능성  

# 7. 컬렉션  

# 8. 람다식  
