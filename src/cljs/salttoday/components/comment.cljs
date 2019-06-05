(ns salttoday.components.comment
  (:require [cuerdas.core :as string]))

(def blue "#0072bc")
(def red "#ed1c24")
(def gray "#636363")
(def border-size "2 / 4px")

(defn comment-gradient [upvotes downvotes pos-gradient neg-gradient]
  (cond
    (and (= downvotes 0) (= upvotes 0)) (string/format "linear-gradient(90deg, %s, %s) %s" gray gray border-size)
    (= downvotes 0) (string/format "linear-gradient(90deg, %s, %s) %s" blue blue border-size)
    (= upvotes 0) (string/format "linear-gradient(90deg, %s, %s) %s" red red border-size)
    :else (string/format "linear-gradient(90deg, %s %s%, %s %s%) %s" blue pos-gradient red neg-gradient border-size)))

(defn upvote-color [upvotes]
  (if (= upvotes 0)
    gray
    blue))

(defn downvote-color [downvotes]
  (if (= downvotes 0)
    gray
    red))

(defn comment-component [comment]
  (let [upvotes (:upvotes comment)
        downvotes (:downvotes comment)
        vote-total (max 1 (+ upvotes downvotes))
        pos-percent (if (zero? vote-total)
                      50
                      (* 100.0 (/ upvotes vote-total)))
        pos-gradient (if (= pos-percent 100)
                       100
                       (max 0 (- pos-percent 2.5)))
        neg-gradient (min 100 (+ pos-gradient 5))]

    [:div.row
     ; Title
     [:div.article-title
      [:a.article-link {:href (:url comment) :target "_blank"}
       (:title comment)]]
     [:div.row.comment-metadata-row
      ; Comment Body / Link to Article
      [:div.column.comment-body {:style {:flex 70
                                         :border-image (comment-gradient upvotes downvotes pos-gradient neg-gradient)}}
       ; Likes
       [:span.counter.like-counter {:style {:color (upvote-color upvotes)}}
        [:span.fa-stack.fa-1x.counter-icon
         [:i.fas.fa-thumbs-up.fa-stack-2x]
         [:i.fas.fa-stack-1x.vote-counter-text (str upvotes " ")]]]
       ; Comment text
       [:span.comment-text (:text comment)]
       ; Dislikes
       [:span.counter.dislike-counter {:style {:color (downvote-color downvotes)}}
        [:span.fa-stack.fa-1x.counter-icon
         [:i.fas.fa-thumbs-down.fa-stack-2x]
         [:i.fas.fa-stack-1x.vote-counter-text (str downvotes " ")]]]]]
     ; Author Information
     [:div.row
      ; Empty Offset
      [:div.column.comment-author {:style {:flex 70}}
       [:a.author-link {:href "/#/user"} "- "
        (:user comment)]]
      ; Empty Offset
      [:div.column {:style {:flex 15}}]]]))