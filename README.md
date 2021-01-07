# Formally Verified Chisel Designs

This repository contains some examples of Chisel circuits that can be verified using the Chisel/FIRRTL extension we implemented as final project for the CS-550 : Formal Verification class taught at EPFL in Fall 2020 by Viktor Kunčak [@vkuncak](https://github.com/vkuncak).

## Structure

The examples can be found in the src directory. Our implementations are in the submodules `chisel3` and `chisel3/firrtl`. To clone this repo run 
```
$ git clone git@github.com:Pialex99/CS-550-Final-Project.git --recurse-submodules
```
This will also automatically download the submodules. To verrify the circuits [cvc4](https://cvc4.github.io/) needs to be installed and in `$PATH`, then simply run `sbt run` with the corresponding circuit uncommended in `src/Main.scala`.

## Authors

- Alexandre Pinazza - [@Pialex99](https://github.com/Pialex99)
- Axel Marmet - [@axelmarmet](https://github.com/axelmarmet)
- Andra Bisca - [@Amyst25](https://github.com/Amyst25)
