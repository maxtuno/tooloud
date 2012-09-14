(ns tooloud.samples
  (:use [clojure.core.match :only [match]]
        [overtone.live]))

(def _ false)
(def X true)

(defn- play-sample
  [samp time vol]
  (at time (stereo-player samp :vol vol)))

(defn determine-time
 [onset-time b-idx beat-dur num-beats]
 (+ onset-time (* b-idx beat-dur)))

(defn- schedule-all-beats
  [bar samp onset-time bar-dur]
  (let [num-beats (count bar)
        beat-dur (/ bar-dur num-beats)]
    (doall
     (map-indexed (fn [idx beat]
                    (cond
                     (= true beat)
                     (play-sample samp (determine-time onset-time idx beat-dur num-beats) 10)

                     (number? beat)
                     (play-sample samp (determine-time onset-time idx beat-dur num-beats) (/ beat 10))

                     (sequential? beat)
                     (schedule-all-beats beat
                                         samp
                                         (determine-time onset-time idx beat-dur num-beats)
                                         beat-dur)))
                  bar))))

(defn play-rhythm
  ([patterns* bar-dur*] (play-rhythm patterns* bar-dur* (+ 500 (now)) 0))
  ([patterns* bar-dur* start-time beat-num]
     (let [patterns @patterns*
           bar-dur @bar-dur*]
       (doall
        (map (fn [[key [samp pat]]]
               (let [idx (mod beat-num (count pat))]
                 (schedule-all-beats (nth pat idx) samp start-time bar-dur)))
             patterns))
       (apply-at (+ start-time bar-dur) #'play-rhythm [patterns*
                                                       bar-dur*
                                                       (+ start-time bar-dur)
                                                       (inc beat-num)]))))

(def clap  (load-sample "~/Desktop/DUBSTEP/Kit/K_Cabasa_A3.wav"))
(def cy    (load-sample "~/Desktop/DUBSTEP/Kit/K_Ride_Cymbal_1_D#2.wav"))
(def bass  (load-sample "~/Desktop/DUBSTEP/Kit/Bass.wav"))
(def snar  (load-sample "~/Desktop/DUBSTEP/Kit/K_AcSnare_D1.wav"))
(def hhos  (load-sample "~/Desktop/DUBSTEP/Kit/K_Closed_Hihat_F#1.wav"))
(def hho2  (load-sample "~/Desktop/DUBSTEP/Kit/K_Pedal_Hihat_G#1.wav"))

(def kit [clap cy bass snar hhos hho2])
                  
(def _ false)
(def X true)

(def patterns* (atom   {:clap  [clap  [[_]]]
                        :cy    [cy    [[_]]]
                        :bass  [bass  [[_]]]
                        :snare [snar  [[_]]]
                        :hhos  [hhos  [[_]]]
                        :hho2  [hho2  [[_]]]}))




