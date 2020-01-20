# The Laboratory AI -Tochka-

※「Lily02」は開発初期のコードネームです．

## 概要

Tochka は田島先生作「The Laboratory」をプレイする AI を「NN+GA」と呼ばれるアルゴリズムで作成したものです．
入力層と中間層二層，出力層の四層からなるネットワークとなっており，層ごとのシナプス係数を GA で学習させています．
この中には，ゲームをプレイするクライアントの他，そのソースコード，シナプス係数を学習する部分のソースコードが入っています．

## 環境

- SDK 1.8
- NetBeans

## 内容物

- executableJar
  - images
    - ゲームサーバが利用する画像ファイル群です．移動させないでください．
  - SampleAI2019.jar
    - 適当な動きをする AI です．対戦相手にご利用ください．
  - SampleWeight1.csv
  - SampleWeight2.csv
  - SampleWeight3.csv
    - サンプルの係数ファイルです．そこそこ強いです．
  - TheLaboratoryServer.jar
    - ゲームサーバです．
  - Tochka.jar
    - 今回作成した AI です．使い方は後述します．
- Lily02Learning
  - シナプス係数を学習する部分のソースコードです．NetBeans で開いてください．
- SimpleClientLily02
  - 今回作成した AI のソースコードです．NetBeans で開いてください．
- readme.md
  - このファイルです．

## AI(Tochka)の使い方

(予めゲームサーバを起動しておいてください．)

1. Tochka.jar を起動してください．
2. 「AI Mode」にチェックを入れてください．
3. 「Weight File」の「Choose」ボタンを押してシナプス係数の csv ファイルを選択してください．
4. address と port がゲームサーバと同じになっていることを確認して「Connect」を押してください．
5. あとは自動でプレイが進みます．

SampleAI は手順 3.がなくなるだけです．
