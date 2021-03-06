#
# Sonar, entreprise quality control tool.
# Copyright (C) 2008-2013 SonarSource
# mailto:contact AT sonarsource DOT com
#
# SonarQube is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# SonarQube is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program; if not, write to the Free Software Foundation,
# Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#

class ActionPlan < ActiveRecord::Base
  belongs_to :project
  has_and_belongs_to_many :reviews

  validates_length_of :name, :within => 1..200
  validates_length_of :description, :maximum => 1000, :allow_blank => true, :allow_nil => true
  validates_presence_of :user_login, :message => "can't be empty"
  validates_presence_of :status, :message => "can't be empty"
  validates_presence_of :project, :message => "can't be empty"
  validate :unique_name_on_same_project

  STATUS_OPEN = 'OPEN'
  STATUS_CLOSED = 'CLOSED'

  def self.open_by_project_id(project_id)
    ActionPlan.find :all, :conditions => ['status=? AND project_id=?', STATUS_OPEN, project_id], :order => :name
  end

  def self.find_by_key(key)
    ActionPlan.first :conditions => ['kee=?', key]
  end

  def key
    kee
  end

  def user
    @user ||=
      begin
        user_login ? User.find(:first, :conditions => ['login=?', user_login]) : nil
      end
  end

  def closed?
    status == STATUS_CLOSED
  end

  def open?
    status == STATUS_OPEN
  end

  def progress
    total_reviews = reviews.size
    open_reviews = reviews.select { |r| r.open? || r.reopened? }.size
    {:total => total_reviews, :open => open_reviews, :resolved => total_reviews-open_reviews}
  end

  def has_open_reviews?
    open_reviews.size > 0
  end

  def open_reviews
    reviews.select { |r| r.open? || r.reopened? }
  end

  def over_due?
    deadline ? status==STATUS_OPEN && deadline.past? : false
  end

  # since 3.6
  def self.to_hash(java_action_plan)
    hash = {:key => java_action_plan.key(), :name => java_action_plan.name(), :status => java_action_plan.status()}
    hash[:desc] = java_action_plan.description() if java_action_plan.description() && !java_action_plan.description().blank?
    hash[:userLogin] = java_action_plan.userLogin() if java_action_plan.userLogin()
    hash[:deadLine] = Api::Utils.format_datetime(java_action_plan.deadLine()) if java_action_plan.deadLine()
    hash[:createdAt] = Api::Utils.format_datetime(java_action_plan.createdAt()) if java_action_plan.createdAt()
    hash[:updatedAt] = Api::Utils.format_datetime(java_action_plan.updatedAt()) if java_action_plan.updatedAt()
    hash
  end

  private

  def unique_name_on_same_project
    action_plan = ActionPlan.find(:first, :conditions => ['project_id=? AND name=?', project_id, name])
    if action_plan && ((id && action_plan.id!=id) || !id)
      errors.add(:base, Api::Utils.message('action_plans.same_name_in_same_project'))
    end
  end

end
