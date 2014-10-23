(ns com.gfredericks.qubits.examples.nats-arithmetic-test
  (:require [clojure.test :refer :all]
            [com.gfredericks.qubits.examples.nats-arithmetic :refer :all]
            [com.gfredericks.qubits.objects :as q]))

(defn n->qs
  [bits n]
  (let [qs (qvec bits)]
    (loop [i 0 n n]
      (when (< i bits)
        (if (odd? n) (q/X (qs i)))
        (recur (inc i) (quot n 2))))
    qs))

(defn qs->n
  [qs]
  (apply + (map * (map q/observe qs) (iterate #(* % 2) 1))))

(deftest addition-test
  (dotimes [_ 10]
    (let [as (n->qs 5 0)
          bs (n->qs 5 0)
          cs (n->qs 6 0)]
      (dotimes [i (count as)] (q/H (as i)))
      (dotimes [i (count bs)] (q/H (bs i)))
      (add as bs cs)
      (is (= (qs->n cs)
             (+ (qs->n as)
                (qs->n bs)))))))

(deftest multiplication-test
  (dotimes [_ 10]
    (let [as (n->qs 5 0)
          bs (n->qs 5 0)
          cs (n->qs 10 0)]
      (dotimes [i (count as)]
        (when (zero? (rand-int 3))
          (q/H (as i))))
      (dotimes [i (count bs)]
        (when (zero? (rand-int 3))
          (q/H (bs i))))
      (multiply as bs cs)
      (is (= (qs->n cs)
             (* (qs->n as)
                (qs->n bs)))))))