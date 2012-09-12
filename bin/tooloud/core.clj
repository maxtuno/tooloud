(ns tooloud.core
  (:use [overtone.live]
        [tooloud.synthesizers]
        [tooloud.patterns]
        [tooloud.rhythmic]))

;kit 1
(def k1 (kicki))
(def s1 (snare :bpm 140))

;kit 2
(def k2 (kickii))
(def s2 (snare :bpm 280))

;kit 3 (11!!!)
(def k3 (kickiii))
(def s3 (snare :bpm 560))


;kills
(kill k1)
(kill k2)
(kill k3)

(kill s1)
(kill s2)
(kill s3)
    
;woobles

(def dub1 (dub-base-i))
(def dub2 (dub-base-ii))

(ctl dub2 :wobble 3)
(ctl dub2 :note 80)
(ctl dub2 :note 40)


;kill woobles

(kill dub1)
(kill dub2)

; Fill
(def fill (p (cycle (pattern derezzed 140))))

;stop
(stop)

; background
(def base
  (demo 140
      (let [bpm 140
       notes [40 41 28 28 40 27 25 80 79 80 30 85 40 78 25 80 79 80]
       trig (impulse:kr (/ bpm 140))
       freq (midicps (lag (demand trig 0 (dxrand notes INF)) 0.25))
       swr (demand trig 0 (dxrand [1 6 6 2 1 2 4 8 6 3 16 1 6 6 2 1 2 4 8 6 3] INF))
       sweep (lin-exp (lf-tri swr) -1 1 [40 30] 3000)
       wob (apply + (wobble-saw (* freq [0.99 1.01])))
       wob (lpf wob sweep)
       wob (* 1 (normalizer wob))
       wob (+ wob (bpf wob 1500 2))
       wob (+ wob (* 0.2 (g-verb wob 9 0.7 0.7)))]

   (clip2 (+ wob) 1))))

;kill background
(kill base)

;stop all
(stop)