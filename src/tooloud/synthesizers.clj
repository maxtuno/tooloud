(ns tooloud.synthesizers
  (:use [overtone.live]))

(defcgen detuned-saw
  "A detuned saw wave."
  [freq {:default 40 :expands? true}]
  (:ar (->> freq
            (* [0.99 1.01])
            (saw)
            (apply +))))

(defcgen wobble
  "wobble the the input"
  [in   {:doc "input source to wobble"
         :default 0}
   freq {:doc "wobble frequency"
         :default 1}]
  (:ar (let [sweep (lf-tri freq)
             sweep (lin-exp sweep -1 1 40 3000)]
         (lpf in sweep))))

(defcgen wobble-saw
  "Generate a wobbly, detuned saw wave!"
  [freq     {:doc "Main frequency"
             :default 40}
   wob-freq {:doc "Wobble frequency"
             :default 0.5}]
  (:ar (-> (detuned-saw freq)
           (wobble wob-freq)
           normalizer)))

(defsynth dub-base-i [out-bus 0 bpm 140 wobble 6 note 30 v 2]
 (let [trig (impulse:kr (/ bpm 140))
       freq (midicps note)
       swr (demand trig 0 (dseq [wobble] INF))
       sweep (lin-exp (lf-tri swr) -1 1 40 3000)
       wob (apply + (wobble-saw (* freq [0.99 1 1.01])))
       wob (lpf wob sweep)
       wob (* 0.9 (normalizer wob))
       wob (+ wob (bpf wob 1500 2))
       wob (+ wob (* 0.2 (g-verb wob 9 0.7 0.7)))]

   (out out-bus    (* v (clip2 (+ wob) 1)))))

(defsynth dub-base-ii [out-bus 0 bpm 140 wobble 3 note 40  v 2]
 (let [trig (impulse:kr (/ bpm 140))
       freq (midicps note)
       swr (demand trig 0 (dseq [wobble] INF))
       sweep (lin-exp (lf-tri swr) -1 1 40 3000)
       wob (apply + (saw (* freq [0.99 1.01])))
       wob (lpf wob sweep)
       wob (* 0.9 (normalizer wob))
       wob (+ wob (bpf wob 1500 2))
       wob (+ wob (* 0.2 (g-verb wob 9 0.7 0.7)))]

   (out out-bus    (* v (clip2 (+ wob) 1)))))

(defn ugen-cents
  "Returns a frequency computed by adding n-cents to freq.  A cent is a
  logarithmic measurement of pitch, where 1-octave equals 1200 cents."
  [freq n-cents]
  (with-overloaded-ugens
    (* freq (pow 2 (/ n-cents 1200)))))

(definst rise-fall-pad [freq 440 split -5 t 4]
  (let [f-env (env-gen (perc t t) 1 1 0 1 FREE)]
    (rlpf (* 0.3 (saw [freq (ugen-cents freq split)]))
          (+ (* 0.6 freq) (* f-env 2 freq)) 0.2)))

(definst resonant-pad [freq 440 split -5 t 4 lfo 0.5 depth 10]
  (let [f-env (env-gen (perc t t) 1 1 0 1 FREE)
        lfo (* depth (sin-osc:kr lfo))]
    (rlpf (* 0.3 (+ (square freq) (lf-tri (+ lfo (ugen-cents freq split)))))
          (+ (* 0.8 freq) (* f-env 2 freq)) 3/4)))

