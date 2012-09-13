(ns tooloud.fx
  (:use [overtone.live]))

(defsynth compressor-tooloud [in-bus 10]
  (let [source (in in-bus)]
    (out 0 (pan2 (compander source source (mouse-y:kr 0.0 1) 1 0.5 0.01 0.01)))))

(defsynth reverb-tooloud [in-bus 10]
  (out 0 (pan2 (free-verb (in in-bus) 0.5 (mouse-y:kr 0.0 1) (mouse-x:kr 0.0 1)))))

(defsynth limiter-tooloud [in-bus 10]
  (let [source (in in-bus)]
    (out 0 (pan2 (compander source source (mouse-y:kr 0.0 1) 1 0.1 0.01 0.01)))))